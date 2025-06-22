package com.furniture.miley.sales.service;

import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.service.ProductService;
import com.furniture.miley.exception.customexception.*;
import com.furniture.miley.sales.dto.order.preparation.*;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PreparationStatus;
import com.furniture.miley.sales.enums.ShippingStatus;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderPreparation;
import com.furniture.miley.sales.model.order.OrderShipping;
import com.furniture.miley.sales.repository.order.OrderPreparationRepository;
import com.furniture.miley.sales.repository.order.OrderShippingRepository;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import com.furniture.miley.warehouse.enums.GrocerStatus;
import com.furniture.miley.warehouse.enums.InventoryMovementType;
import com.furniture.miley.warehouse.model.ExitGuide;
import com.furniture.miley.warehouse.model.Grocer;
import com.furniture.miley.warehouse.model.InventoryMovements;
import com.furniture.miley.warehouse.model.Warehouse;
import com.furniture.miley.warehouse.repository.ExitGuideRepository;
import com.furniture.miley.warehouse.repository.InventoryMovementsRepository;
import com.furniture.miley.warehouse.service.GrocerService;
import com.furniture.miley.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPreparationService {

    private final OrderShippingRepository orderShippingRepository;
    private final OrderPreparationRepository orderPreparationRepository;
    private final GrocerService grocerService;
    private final InventoryMovementsRepository inventoryMovementsRepository;
    private final WarehouseService warehouseService;
    private final ExitGuideRepository exitGuideRepository;

    private final OrderService orderService;
    private final ProductService productService;
    private final UserService userService;

    public OrderPreparation findById(String id) throws ResourceNotFoundException {
        return orderPreparationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Preparacion de pedido no encontrada"));
    }

    public OrderPreparation findByOrder(Order order) throws ResourceNotFoundException {
        return orderPreparationRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Preparacion de pedido no encontrada"));
    }

    public List<OrderPreparationDTO> getAll(){
        Sort sort = Sort.by( Sort.Direction.DESC, "createdDate" );
        return orderPreparationRepository.findAll(sort).stream().map(OrderPreparationDTO::toDTO).toList();
    }

    public OrderPreparationDTO startPreparationOrder(StartOrderPreparationDTO startOrderPreparationDTO) throws ResourceNotFoundException, AlreadyStartedProcessException, FinishCurrentProcessException, AbortedProcessException {
        Order order = orderService.findById(startOrderPreparationDTO.orderId());
        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido que intenta preparar fue anulado","OrderPreparation");
        }

        User user = userService.findById(startOrderPreparationDTO.userId());
        Grocer grocer = grocerService.findByUser(user);

        if( grocer.getStatus() == GrocerStatus.DISPONIBLE ){
            OrderPreparation orderPreparation = this.findByOrder( order );

            if(orderPreparation.getStatus() == PreparationStatus.PENDIENTE ){
                orderPreparation.setStatus(PreparationStatus.EN_PREPARACION);
                orderPreparation.setStartDate(new Timestamp(new Date().getTime()));

                grocer.setStatus( GrocerStatus.PROCESANDO_PEDIDO );
                order.setStatus( OrderStatus.IN_PROGRESS );

                orderPreparation.setGrocer(grocer);
                orderPreparation.setOrder(order);

                /*"Se inicio el proceso de preparacion del pedido: #"+order.getId()*/

                return OrderPreparationDTO.toDTO(orderPreparationRepository.save( orderPreparation ));
            }else {
                throw new AlreadyStartedProcessException("Este pedido ya fue iniciado por otro trabajador", "OrderPreparation");
            }
        }else {
            throw new FinishCurrentProcessException("Para iniciar con otro pedido debe culminar el que ya inicio.", "OrderPreparation");
        }
    }

    public OrderPreparationDTO checkOrderPreparationPackaging(PackagingOrderPreparationDTO packagingOrderPreparationDTO) throws ResourceNotFoundException, AbortedProcessException {
        OrderPreparation orderPreparation = this.findById( packagingOrderPreparationDTO.preparationOrderId() );
        Grocer grocer = grocerService.findById( orderPreparation.getGrocer().getId() );
        Order order = orderService.findById( orderPreparation.getOrder().getId() );

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido fue anulado", "OrderPreparation");
        }

        if(orderPreparation.getStatus() != PreparationStatus.EN_PREPARACION ){
            throw new PrevStatusRequiredException("La preparacion del pedido debe pasar por el estado EN_PREPARACION para continuar", PreparationStatus.EN_PREPARACION.name());
        }

        orderPreparation.setStatus( PreparationStatus.EN_EMPAQUETADO );
        orderPreparation.setPreparedDate(new Timestamp(new Date().getTime()));

        grocer.setStatus( GrocerStatus.EMPAQUETANDO );
        orderPreparation.setGrocer(grocer);
        OrderPreparation orderPreparationUpdated = orderPreparationRepository.save( orderPreparation );

        /*"Pedido en empaquetado"*/
        return OrderPreparationDTO.toDTO( orderPreparationUpdated );
    }

    public OrderPreparationDTO checkOrderPreparationCompleted(CompletedOrderPreparationDTO completedOrderPreparationDTO) throws ResourceNotFoundException, AbortedProcessException {
        OrderPreparation orderPreparation = this.findById( completedOrderPreparationDTO.orderPreparationId() );
        Grocer grocer = grocerService.findById( orderPreparation.getGrocer().getId() );
        Order order = orderService.findById( orderPreparation.getOrder().getId() );
        Warehouse warehouse = warehouseService.findById(completedOrderPreparationDTO.warehouse() );

        if( order.getStatus() == OrderStatus.ANULADO){
            throw new AbortedProcessException("El pedido fue anulado", "OrderPreparation");
        }

        if(orderPreparation.getStatus() != PreparationStatus.EN_EMPAQUETADO ){
            throw new PrevStatusRequiredException("El pedido debe pasar por el estado EN_EMPAQUETADO para continuar", PreparationStatus.EN_EMPAQUETADO.name());
        }

        ExitGuide newExitGuide = ExitGuide.builder()
                .date(new Timestamp(System.currentTimeMillis()))
                .observations(completedOrderPreparationDTO.observations())
                .order(order)
                .grocer( grocer )
                .warehouse( warehouse )
                .build();

        ExitGuide exitGuideCreated = exitGuideRepository.save( newExitGuide );

        List<InventoryMovements> newInventoryMovementsList = new ArrayList<>();
        order.getOrderDetails().forEach(o -> {
            Product product = null;
            try {
                product = productService.findById(  o.getProduct().getId() );
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
            int newStock = product.getStock() - o.getAmount();

            InventoryMovements inventoryMovements = InventoryMovements.builder()
                    .date( new Timestamp(System.currentTimeMillis()))
                    .type(InventoryMovementType.SALIDA)
                    .reason("Venta")
                    .initialStock( product.getStock() )
                    .amount( o.getAmount() )
                    .newStock( newStock )
                    .exitGuide( exitGuideCreated )
                    .warehouse( warehouse )
                    .build();

            product.setStock( newStock );
            inventoryMovements.setProduct(product);
            //productRepository.save( product );

            newInventoryMovementsList.add( inventoryMovements );
        });

        inventoryMovementsRepository.saveAll( newInventoryMovementsList );

        orderPreparation.setStatus( PreparationStatus.LISTO_PARA_RECOGER );
        orderPreparation.setCompletedDate(new Timestamp(new Date().getTime()));
        order.setStatus(OrderStatus.PREPARADO);

        grocer.setStatus(GrocerStatus.DISPONIBLE);

        orderPreparation.setGrocer( grocer );

        OrderShipping newOrderShipping = OrderShipping.builder()
                .order( order )
                .preparedBy( grocer.getUser().getPersonalInformation().getFullName() )
                .address( order.getShippingAddress() )
                .distance( order.getDistance() )
                .createdDate( new Timestamp(new Date().getTime()))
                .status(ShippingStatus.PENDIENTE)
                .build();

        OrderShipping orderShippingCreated = orderShippingRepository.save( newOrderShipping );

        order.setOrderShipping( orderShippingCreated );
        order.setExitGuide( exitGuideCreated );

        orderPreparation.setOrder(order);

        //orderRepository.save( order );
        OrderPreparation orderPreparationUpdated = orderPreparationRepository.save( orderPreparation );
        /*"Se completo el proceso de preparacion del pedido"*/
        return OrderPreparationDTO.toDTO( orderPreparationUpdated );
    }

    public DetailedPreparationOrder getDetailsById(String id) throws ResourceNotFoundException {
        return DetailedPreparationOrder.toDTO(this.findById( id ));
    }
}
