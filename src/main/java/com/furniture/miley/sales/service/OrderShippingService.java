package com.furniture.miley.sales.service;

import com.furniture.miley.delivery.enums.CarrierStatus;
import com.furniture.miley.delivery.model.Carrier;
import com.furniture.miley.delivery.service.CarrierService;
import com.furniture.miley.exception.customexception.*;
import com.furniture.miley.sales.dto.order.shipping.*;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderShipping;
import com.furniture.miley.sales.repository.order.OrderShippingRepository;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderShippingService {

    private final OrderShippingRepository orderShippingRepository;

    private final CarrierService carrierService;
    private final OrderService orderService;
    private final UserService userService;

    public OrderShipping findById(String id) throws ResourceNotFoundException {
        return orderShippingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega de pedido no encontrada"));
    }

    public List<OrderShippingDTO> getAll(){
        Sort sort = Sort.by( Sort.Direction.DESC, "createdDate" );
        return orderShippingRepository.findAll(sort).stream().map(OrderShippingDTO::toDTO).toList();
    }

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

    public OrderShippingDTO checkOrderShippingTransit(TransitOrderShippingDTO transitOrderShippingDTO) throws ResourceNotFoundException, AbortedProcessException {
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

        OrderShipping orderShippingUpdated = orderShippingRepository.save( orderShipping );
        /*"Proceso de envio culminado, se entrego el pedido."*/
        return OrderShippingDTO.toDTO( orderShippingUpdated );
    }

    public DetailedShippingOrder getDetailsById(String id) throws ResourceNotFoundException {
        OrderShipping orderShipping = this.findById( id );
        return DetailedShippingOrder.toDTO(orderShipping);
    }
}
