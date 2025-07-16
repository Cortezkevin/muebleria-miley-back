package com.furniture.miley.sales.service;

import com.furniture.miley.delivery.enums.CarrierStatus;
import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.delivery.model.OrderShippingLocation;
import com.furniture.miley.delivery.service.CarrierService;
import com.furniture.miley.exception.customexception.*;
import com.furniture.miley.profile.dto.notification.NewNotificationDTO;
import com.furniture.miley.profile.service.NotificationService;
import com.furniture.miley.sales.dto.order.OrderDTO;
import com.furniture.miley.sales.dto.order.shipping.*;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderShipping;
import com.furniture.miley.sales.repository.order.OrderShippingRepository;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import com.google.firebase.messaging.*;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderShippingService {

    private final OrderShippingRepository orderShippingRepository;

    private final CarrierService carrierService;
    private final OrderService orderService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final OrderShippingLocationService orderShippingLocationService;

    public OrderShipping findById(String id) throws ResourceNotFoundException {
        return orderShippingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega de pedido no encontrada"));
    }

    public List<OrderShippingDTO> getAll(){
        Sort sort = Sort.by( Sort.Direction.DESC, "createdDate" );
        return orderShippingRepository.findAll(sort).stream().map(OrderShippingDTO::toDTO).toList();
    }

    public List<OrderDTO> getAllReadyToSend() throws ResourceNotFoundException {
        MainUser mainUser = (MainUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(mainUser.getEmail());
        Carrier carrier = carrierService.findByUser(user);
        Sort sort = Sort.by( Sort.Direction.DESC, "createdDate" );
        return orderService.findAll(sort).stream()
                .filter(order -> {
                    Optional<OrderShipping> os = orderShippingRepository.findByOrder(order);
                    if(os.isEmpty()){
                        return false;
                    }else {
                        if(os.get().getStatus().equals(ShippingStatus.PREPARADO) && os.get().getCarrier().getId().equals(carrier.getId())){
                            return true;
                        }else {
                            return false;
                        }
                    }
                })
                .map(OrderDTO::toDTO)
                .toList();
    }
/*
    public List<OrderShippingDTO> getAllReadyToSend() throws ResourceNotFoundException {
        MainUser mainUser = (MainUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(mainUser.getEmail());
        Carrier carrier = carrierService.findByUser(user);
        Sort sort = Sort.by( Sort.Direction.DESC, "createdDate" );
        return orderShippingRepository.findAll(sort).stream()
                .filter(order -> order.getStatus().equals(ShippingStatus.PREPARADO))
                .filter(order -> order.getCarrier().getId().equals(carrier.getId()))
                .map(OrderShippingDTO::toDTO)
                .toList();
    }*/

    public OrderShippingDTO startShippingOrder(StartOrderShippingDTO startOrderShippingDTO) throws ResourceNotFoundException, AbortedProcessException, AlreadyStartedProcessException, FinishCurrentProcessException {
        Order order = orderService.findById(startOrderShippingDTO.orderId());

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido que intenta preparar fue anulado","OrderShipping");
        }

        User user = userService.findById(startOrderShippingDTO.userId());
        Carrier carrier = carrierService.findByUser(user);

        if( carrier.getStatus() == CarrierStatus.FUERA_DE_SERVICIO){
            throw new PrevStatusRequiredException("No puede iniciar un proceso si esta fuera de servicio",CarrierStatus.FUERA_DE_SERVICIO.name());
        }

        if( carrier.getStatus() == CarrierStatus.DISPONIBLE ){
            OrderShipping orderShipping = orderShippingRepository.findByOrder( order ).orElseThrow(() -> new ResourceNotFoundException("Entrega de pedido no encontrado"));

            if(orderShipping.getStatus() == ShippingStatus.PENDIENTE ){
                orderShipping.setCarrier( carrier );
                orderShipping.setStatus(ShippingStatus.EN_PREPARACION);
                orderShipping.setStartDate( new Timestamp(new Date().getTime()));

                carrier.setStatus( CarrierStatus.PROCESANDO_PEDIDO );

                orderShipping.setCarrier( carrier );
                OrderShipping orderShippingUpdated = orderShippingRepository.save( orderShipping );
                /*"Se inicio el proceso de entrega del pedido: #"+order.getId()*/
                return OrderShippingDTO.toDTO( orderShippingUpdated );
            }else {
                throw new AlreadyStartedProcessException("Este pedido ya fue iniciado por otro repartidor","OrderShipping");
            }
        }else {
            throw new FinishCurrentProcessException("Para iniciar con otro pedido debe culminar el que ya inicio.","OrderShipping");
        }
    }

    public OrderShippingDTO checkOrderShippingPrepared(PreparedOrderShippingDTO preparedOrderShippingDTO) throws ResourceNotFoundException, AbortedProcessException {
        OrderShipping orderShipping = this.findById( preparedOrderShippingDTO.orderShippingId() );
        Order order = orderService.findById( orderShipping.getOrder().getId() );

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido fue anulado","OrderShipping");
        }

        if(orderShipping.getStatus() != ShippingStatus.EN_PREPARACION ){
            throw new PrevStatusRequiredException("El pedido debe pasar por el estado EN_PREPARACION para continuar", ShippingStatus.EN_PREPARACION.name());
        }

        orderShipping.setStatus( ShippingStatus.PREPARADO );
        orderShipping.setPreparedDate(new Timestamp(new Date().getTime()));

        OrderShipping orderShippingUpdated = orderShippingRepository.save( orderShipping );

        return OrderShippingDTO.toDTO( orderShippingUpdated );
    }

    public OrderShippingDTO checkOrderShippingTransit(TransitOrderShippingDTO transitOrderShippingDTO) throws ResourceNotFoundException, AbortedProcessException, StripeException, FirebaseMessagingException {
        OrderShipping orderShipping = this.findById( transitOrderShippingDTO.orderShippingId() );
        Carrier carrier = carrierService.findById( orderShipping.getCarrier().getId() );
        Order order = orderService.findById( orderShipping.getOrder().getId() );

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido fue anulado","OrderShipping");
        }

        if(orderShipping.getStatus() != ShippingStatus.PREPARADO ){
            throw new PrevStatusRequiredException("El pedido debe pasar por el estado PREPARADO para continuar", ShippingStatus.PREPARADO.name());
        }

        orderShipping.setStatus( ShippingStatus.EN_TRANSITO );
        orderShipping.setShippingDate(new Timestamp(new Date().getTime()));
        order.setStatus(OrderStatus.SENT);

        carrier.setStatus(CarrierStatus.EN_RUTA);
        orderShipping.setCarrier( carrier );
        orderShipping.setOrder(order);

        // TODO: CREAR OBJETO DE UBICACION DEL PEDIDO
        orderShippingLocationService.save(OrderShippingLocation.builder()
                .lta(transitOrderShippingDTO.lta())
                .lng(transitOrderShippingDTO.lng())
                .build());

        /*TODO: NOTIFICAR AL CLIENTE QUE SU PEDIDO YA INICIO EL RECORRIDO*/
        if( order.getUser().getNotificationWebToken() != null || order.getUser().getNotificationMobileToken() != null ){
            notificationService.sendNotificationTo(
                    order.getUser(),
                    new NewNotificationDTO(
                            "Pedido en camino",
                            "Su pedido ya inicio el recorrido para llegar a sus manos, haga seguimiento tocando este mensaje.",
                            null
                    )
            );
        }

        OrderShipping orderShippingUpdated = orderShippingRepository.save( orderShipping );
        /*"Se inicio el envio del Pedido"*/
        return OrderShippingDTO.toDTO( orderShippingUpdated );
    }

    public OrderShippingDTO checkOrderShippingComplete(CompleteOrderShippingDTO completeOrderShippingDTO) throws ResourceNotFoundException, AbortedProcessException {
        OrderShipping orderShipping = this.findById( completeOrderShippingDTO.orderShippingId() );
        Carrier carrier = carrierService.findById( orderShipping.getCarrier().getId() );
        Order order = orderService.findById( orderShipping.getOrder().getId() );

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido fue anulado","OrderShipping");
        }

        if(orderShipping.getStatus() != ShippingStatus.EN_TRANSITO ){
            throw new PrevStatusRequiredException("El pedido debe pasar por el estado EN_TRANSITO para continuar", ShippingStatus.EN_TRANSITO.name());
        }

        orderShipping.setStatus( ShippingStatus.ENTREGADO );
        orderShipping.setCompletedDate(new Timestamp(new Date().getTime()));
        order.setStatus(OrderStatus.ENTREGADO);
        order.setCompletedDate(new Timestamp(new Date().getTime()));

        carrier.setStatus(CarrierStatus.EN_DESCANSO);
        orderShipping.setCarrier( carrier );
        orderShipping.setOrder(order);

        /*TODO: NOTIFICAR AL CLIENTE QUE SU PEDIDO YA LLEGO A SU DESTINO Y CONFIRMAR LA RECEPCION DE SU PEDIDO*/

        OrderShipping orderShippingUpdated = orderShippingRepository.save( orderShipping );
        /*"Proceso de envio culminado, se entrego el pedido."*/
        return OrderShippingDTO.toDTO( orderShippingUpdated );
    }

    public DetailedShippingOrder getDetailsById(String id) throws ResourceNotFoundException {
        OrderShipping orderShipping = this.findById( id );
        return DetailedShippingOrder.toDTO(orderShipping);
    }

    public String confirmOrderReception(String orderId) throws ResourceNotFoundException {
        Order order = orderService.findById( orderId );
        OrderShipping orderShipping = orderShippingRepository.findByOrder(order).orElseThrow(() -> new ResourceNotFoundException("Entrega de pedido no encontrado"));
        orderShipping.setConfirmFromUser(true);
        return "Se confirmo la recepcio del pedido";
    }

    public void sendTestNotification(String title) throws ResourceNotFoundException, FirebaseMessagingException {
        MainUser mainUser = (MainUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(mainUser.getEmail());
        if( user.getNotificationMobileToken() != null || user.getNotificationWebToken() != null){
            String userRegistrationToken = user.getNotificationMobileToken();
            Message message = Message.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody("Esta es una notificacion de prueba")
                                    .build()
                    )
                    /*.setAndroidConfig(
                            AndroidConfig.builder()
                                    .setNotification(AndroidNotification.builder()
                                            .setClickAction("order_intent")
                                            .build())
                                    .build()
                    )*/
                    .setToken(userRegistrationToken)
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        }
    }
}
