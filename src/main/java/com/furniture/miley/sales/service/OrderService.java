package com.furniture.miley.sales.service;

import com.furniture.miley.exception.customexception.CannotCancelOrderException;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.order.DetailedOrderDTO;
import com.furniture.miley.sales.dto.order.InvoiceDTO;
import com.furniture.miley.sales.dto.order.OrderDTO;
import com.furniture.miley.sales.dto.order.UpdateDatesDTO;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.repository.order.OrderRepository;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;

    public Order save(Order order){
        return orderRepository.save(order);
    }

    public Order findById(String id) throws ResourceNotFoundException {
        return orderRepository.findById( id )
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
    }

    public List<Order> findAll(Sort sort){
        return orderRepository.findAll(sort);
    }

    public DetailedOrderDTO getDetailsById(String id) throws ResourceNotFoundException {
        return DetailedOrderDTO.toDTO( this.findById(id) );
    }

    public List<OrderDTO> getAll(){
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        return orderRepository.findAll(sort).stream().map( OrderDTO::toDTO ).toList();
    }

    public List<OrderDTO> getByUser(String id) throws ResourceNotFoundException {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        User user = userService.findById(id);
        return orderRepository.findByUser( user, sort ).stream().map( OrderDTO::toDTO ).toList();
    }

    public OrderDTO cancelOrder(String orderId) throws ResourceNotFoundException, CannotCancelOrderException {
        Order order = this.findById( orderId );
        if( order.getStatus().equals(OrderStatus.IN_PROGRESS) || order.getStatus().equals(OrderStatus.PENDING)){
            order.setStatus( OrderStatus.ANULADO );
            return OrderDTO.toDTO(orderRepository.save(order));
        }else {
            throw new CannotCancelOrderException("Ya no puede anular el pedido");
        }
    }

    public InvoiceDTO exportInvoice(String orderId) {
       try {
           Order order = this.findById( orderId );
           File file = ResourceUtils.getFile("classpath:orderInvoice.jasper");
           File imgLogo = ResourceUtils.getFile("classpath:static/LOGO.jpeg");
           JasperReport report = ( JasperReport ) JRLoader.loadObject(file);

           HashMap<String,Object> params = new HashMap<>();
           params.put("logoEmpresa", new FileInputStream( imgLogo ));
           params.put("fullName",order.getUser().getPersonalInformation().getFullName());
           params.put("subtotal",order.getSubtotal());
           params.put("delivery",order.getShippingCost());
           params.put("discount",order.getDiscount());
           params.put("tax",order.getTax());
           params.put("total",order.getTotal());
           params.put("ds",new JRBeanCollectionDataSource(order.getOrderDetails()));

           JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

           byte[] invoice = JasperExportManager.exportReportToPdf( jasperPrint );
           String sdf = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
           StringBuilder stringBuilder = new StringBuilder().append("invoice:");

           ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                   .filename(stringBuilder.append(order.getId()).append("generatedDate:").append(sdf).append(".pdf").toString())
                   .build();

           HttpHeaders headers = new HttpHeaders();
           headers.setContentDisposition( contentDisposition );

           return new InvoiceDTO(
                   invoice.length,
                   invoice,
                   headers
           );
       }catch (ResourceNotFoundException e){
           System.out.println("ERROR: "+ e.getMessage() );
           return null;
       } catch (FileNotFoundException e) {
           System.out.println("ERROR: "+ e.getMessage() );
           return null;
       } catch (JRException e) {
           System.out.println("ERROR: "+ e.getMessage() );
           return null;
       }
    }

    public String updateDates(UpdateDatesDTO updateDates){
        try {
            Order order = this.findById( updateDates.orderId() );
            order.setCreatedDate( updateDates.createdDate() );
            order.setDistance(updateDates.distance() );
            order.getOrderPreparation().setCreatedDate( updateDates.createdDate() );
            order.getOrderPreparation().setStartDate( updateDates.createdDate() );
            order.getOrderPreparation().setPreparedDate( updateDates.createdDate() );

            order.getOrderPreparation().setCompletedDate( updateDates.shippingDate() );
            order.getOrderShipping().setStartDate( updateDates.shippingDate() );
            order.getOrderShipping().setPreparedDate( updateDates.shippingDate() );
            order.getOrderShipping().setShippingDate( updateDates.shippingDate() );
            order.getOrderShipping().setCreatedDate( updateDates.shippingDate() );
            order.getOrderShipping().setDistance(updateDates.distance() );

            order.getOrderShipping().setCompletedDate( updateDates.completedDate() );
            order.setCompletedDate( updateDates.completedDate() );

            /*order.getExitGuide().setDate( updateDates.shippingDate() );
            order.getExitGuide().setInventoryMovementsList(order.getExitGuide().getInventoryMovementsList().stream().map(im -> {
                im.setDate( updateDates.shippingDate() );
                return im;
            }).toList());*/
            orderRepository.save(order);
            return "Fechas actualizadas";
        }catch (ResourceNotFoundException e){
            System.out.println("ERROR: "+ e.getMessage() );
            return "OCurrio un error";
        }
    }
}
