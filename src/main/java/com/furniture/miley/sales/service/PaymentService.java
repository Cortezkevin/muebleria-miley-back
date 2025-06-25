package com.furniture.miley.sales.service;

import com.furniture.miley.config.cloudinary.dto.UploadDTO;
import com.furniture.miley.config.cloudinary.dto.UploadResultDTO;
import com.furniture.miley.config.cloudinary.service.CloudinaryService;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.sales.dto.payment.PaymentIndentResponseDTO;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.enums.PaymentMethod;
import com.furniture.miley.sales.enums.PreparationStatus;
import com.furniture.miley.profile.model.Address;
import com.furniture.miley.sales.model.cart.Cart;
import com.furniture.miley.sales.model.order.Invoice;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.sales.model.order.OrderDetail;
import com.furniture.miley.sales.model.order.OrderPreparation;
import com.furniture.miley.sales.repository.InvoiceRepository;
import com.furniture.miley.sales.repository.order.OrderDetailRepository;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import com.furniture.miley.warehouse.service.InventoryMovementsService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final InventoryMovementsService inventoryMovementsService;
    private final OrderDetailRepository orderDetailRepository;
    private final InvoiceRepository invoiceRepository;

    private final OrderService orderService;
    private final OrderPreparationService preparationService;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final ResourceLoader resourceLoader;

    public String successPayment(String userId, String note, String specificAddress) throws ResourceNotFoundException {
        User user = userService.findById( userId );
        Address userAddress= user.getPersonalInformation().getAddress();
        Cart userCart = user.getCart();

        Date createdDate = new Date();

        Order newOrder = Order.builder()
                .paymentMethod(PaymentMethod.TARJETA)
                .note(note)
                .specificAddress(specificAddress)
                .distance( userCart.getDistance() )
                .shippingAddress( userAddress.getFullAddress() )
                .tax( userCart.getTax() )
                .total( userCart.getTotal() )
                .subtotal( userCart.getSubtotal() )
                .discount( userCart.getDiscount() )
                .shippingCost( user.getCart().getShippingCost())
                .createdDate( new Timestamp(new Date().getTime()))
                .status( OrderStatus.PENDING )
                .user( user )
                .build();

        Order orderCreated = orderService.save( newOrder );

        List<OrderDetail> orderDetailList = new ArrayList<>();
        userCart.getCartItems().forEach(cartItem -> {
            OrderDetail newOrderDetail = OrderDetail.builder()
                    .order( orderCreated )
                    .amount( cartItem.getAmount() )
                    .price( cartItem.getProduct().getPrice())
                    .total(cartItem.getTotal())
                    .name( cartItem.getProduct().getName())
                    .product( cartItem.getProduct() )
                    .build();
            orderDetailList.add( newOrderDetail );
        });

        orderDetailRepository.saveAll( orderDetailList );
        orderCreated.setOrderDetails(orderDetailList);

        //IMPLEMENTAR ENVIO DE MENSAJES AL WHATSAPP

        OrderPreparation orderPreparation = OrderPreparation.builder()
                .order(orderCreated)
                .createdDate(new Timestamp(new Date().getTime()))
                .status(PreparationStatus.PENDIENTE)
                .build();

        OrderPreparation preparationCreated = preparationService.save( orderPreparation );

        orderCreated.setOrderPreparation(preparationCreated);
        orderService.save(orderCreated);

        String invoicePDFUrl = generateAndUploadInvoicePDF( orderCreated.getId() );

        Invoice newInvoice = Invoice.builder()
                .order( orderCreated )
                .url(invoicePDFUrl)
                .issuedDate( new java.sql.Date(createdDate.getTime()).toLocalDate() )
                .build();

        invoiceRepository.save( newInvoice );

        /*"Compra realizada correctamente"*/
        return "Compra realizada correctamente";
    }

    public PaymentIndentResponseDTO createIndent(String userId) throws ResourceNotFoundException, StripeException {
        User user = userService.findById( userId );
        Stripe.apiKey = "sk_test_51LDHfGCjrtAyA6AHlTaXE88uQjaFPSq0EHYWGbsCIiELO6Jt1n1v8PGBPtl4PRlZrOSpl5gK8XC3xTsiusbZqP8D00sPgDAJA2";
        Integer totalInt = user.getCart().getTotal().intValue();
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(totalInt.longValue() * 100)
                        .setCurrency("pen")
                        .setCustomer(user.getClientId())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                        )
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        return new PaymentIndentResponseDTO( paymentIntent.getClientSecret(), paymentIntent.getId() );
    }

    public String cancelIntent(String intentId, String reason) throws ResourceNotFoundException, StripeException {
        Stripe.apiKey = "sk_test_51LDHfGCjrtAyA6AHlTaXE88uQjaFPSq0EHYWGbsCIiELO6Jt1n1v8PGBPtl4PRlZrOSpl5gK8XC3xTsiusbZqP8D00sPgDAJA2";
        PaymentIntent resource = PaymentIntent.retrieve(intentId);
        resource.setCancellationReason(reason);
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder().build();
        PaymentIntent paymentIntent = resource.cancel(params);
        return "Se cancelo la operacion de compra con #ID:" + paymentIntent.getId();
    }

    public String generateAndUploadInvoicePDF(String orderId){
        try {
            Order order = orderService.findById( orderId );
            Resource jasperResource = resourceLoader.getResource("classpath:orderInvoice.jasper");
            Resource logoResource = resourceLoader.getResource("classpath:static/LOGO.jpeg");
            InputStream jasperInputStream  = jasperResource.getInputStream();
            InputStream logoInputStream  = logoResource.getInputStream();
            JasperReport report = ( JasperReport ) JRLoader.loadObject(jasperInputStream);

            HashMap<String,Object> params = new HashMap<>();
            params.put("logoEmpresa", logoInputStream );
            params.put("fullName",order.getUser().getPersonalInformation().getFullName());
            params.put("subtotal",order.getSubtotal());
            params.put("delivery",order.getShippingCost());
            params.put("discount",order.getDiscount());
            params.put("tax",order.getTax());
            params.put("total",order.getTotal());
            params.put("ds",new JRBeanCollectionDataSource(order.getOrderDetails()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

            Path tempPDFPath = Files.createTempFile("invoice_"+order.getId(), ".pdf");
            File tempPDFFile = tempPDFPath.toFile();

            JasperExportManager.exportReportToPdfStream( jasperPrint, new FileOutputStream(tempPDFFile));

            UploadResultDTO uploadResultDTO = cloudinaryService.upload("pdf/"+order.getUser().getId(), new UploadDTO(tempPDFFile, "invoice_"+order.getId()));
            return uploadResultDTO.url();

        }catch (ResourceNotFoundException e){
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } catch (JRException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
