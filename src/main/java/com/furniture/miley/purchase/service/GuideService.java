package com.furniture.miley.purchase.service;

import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.guide.*;
import com.furniture.miley.purchase.model.EntryGuide;
import com.furniture.miley.purchase.repository.EntryGuideRepository;
import com.furniture.miley.purchase.repository.RejectionGuideRepository;
import com.furniture.miley.sales.dto.order.InvoiceDTO;
import com.furniture.miley.warehouse.model.ExitGuide;
import com.furniture.miley.warehouse.repository.ExitGuideRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
public class GuideService {

    private final EntryGuideRepository entryGuideRepository;
    private final ExitGuideRepository exitGuideRepository;
    private final RejectionGuideRepository rejectionGuideRepository;


    public List<EntryGuideDTO> getAllEntryGuides(){
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return entryGuideRepository.findAll(sort).stream().map(EntryGuideDTO::parseToDTO).toList();
    }

    public DetailedEntryGuideDTO getEntryGuideById(String entryGuideId) throws ResourceNotFoundException {
        return entryGuideRepository.findById( entryGuideId ).map( DetailedEntryGuideDTO::parseToDTO )
                .orElseThrow(() -> new ResourceNotFoundException("Guia de entrada no encontrada"));
    }

    public InvoiceDTO exportEntryGuide(String entryGuideId) {
        try {
            EntryGuide entryGuide = entryGuideRepository.findById( entryGuideId ).orElseThrow(() -> new ResourceNotFoundException("Guia de entrada no encontrado"));
            File file = ResourceUtils.getFile("classpath:entryGuide.jasper");
            File imgLogo = ResourceUtils.getFile("classpath:static/LOGO.jpeg");
            JasperReport report = ( JasperReport ) JRLoader.loadObject(file);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = dateFormat.format(entryGuide.getDate());

            HashMap<String,Object> params = new HashMap<>();
            params.put("logoEmpresa", new FileInputStream( imgLogo ));
            params.put("grocer",entryGuide.getGrocer().getUser().getPersonalInformation().getFullName());
            params.put("warehouse",entryGuide.getWarehouse().getLocation());
            params.put("conditions",entryGuide.getProductConditions());
            params.put("createdDate",fechaFormateada);

            if( entryGuide.getPurchaseOrder() != null ){
                params.put("purchaseOrderId",entryGuide.getPurchaseOrder().getId());
                params.put("requester", entryGuide.getPurchaseOrder().getUser().getPersonalInformation().getFullName());
                params.put("requesterPhone", entryGuide.getPurchaseOrder().getUser().getPersonalInformation().getPhone());
                params.put("supplier", entryGuide.getPurchaseOrder().getSupplier().getName());
                params.put("supplierPhone", entryGuide.getPurchaseOrder().getSupplier().getPhone());
            }

            List<EntryGuideMovement> entryGuideMovementList = entryGuide.getInventoryMovementsList().stream().map( EntryGuideMovement::parseToDTO ).toList();
            params.put("ds",new JRBeanCollectionDataSource(entryGuideMovementList));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

            byte[] invoice = JasperExportManager.exportReportToPdf( jasperPrint );
            String sdf = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
            StringBuilder stringBuilder = new StringBuilder().append("entry-guide:");

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(stringBuilder.append(entryGuide.getId()).append("generatedDate:").append(sdf).append(".pdf").toString())
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
            System.out.println("FILE ERROR: "+ e.getMessage() );
            return null;
        } catch (JRException e) {
            System.out.println("JASPER ERROR: "+ e.getMessage() );
            return null;
        }
    }

    public List<ExitGuideDTO> getAllExitGuides(){
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        return exitGuideRepository.findAll(sort).stream().map(ExitGuideDTO::parseToDTO).toList();
    }

    public DetailedExitGuideDTO getExitGuideById(String exitGuideId) throws ResourceNotFoundException {
        return exitGuideRepository.findById( exitGuideId ).map( DetailedExitGuideDTO::parseToDTO )
                .orElseThrow(() -> new ResourceNotFoundException("Guia de salida no encontrada"));
    }

    public InvoiceDTO exportExitGuide(String exitGuideId) {
        try {
            ExitGuide exitGuide = exitGuideRepository.findById( exitGuideId ).orElseThrow(() -> new ResourceNotFoundException("Guia de salida no encontrado"));
            File file = ResourceUtils.getFile("classpath:exitGuide.jasper");
            File imgLogo = ResourceUtils.getFile("classpath:static/LOGO.jpeg");
            JasperReport report = ( JasperReport ) JRLoader.loadObject(file);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String fechaFormateada = dateFormat.format(exitGuide.getDate());

            HashMap<String,Object> params = new HashMap<>();
            params.put("logoEmpresa", new FileInputStream( imgLogo ));
            params.put("grocer",exitGuide.getGrocer().getUser().getPersonalInformation().getFullName());
            params.put("warehouse",exitGuide.getWarehouse().getLocation());
            params.put("conditions",exitGuide.getObservations());
            params.put("createdDate",fechaFormateada);

            if( exitGuide.getOrder() != null ){
                params.put("pedidoId",exitGuide.getOrder().getId());
                params.put("client", exitGuide.getOrder().getUser().getPersonalInformation().getFullName());
                params.put("shippingAddress", exitGuide.getOrder().getShippingAddress());
                params.put("specificAddress", exitGuide.getOrder().getSpecificAddress());
                params.put("note", exitGuide.getOrder().getNote());
                params.put("phone", exitGuide.getOrder().getUser().getPersonalInformation().getPhone());
            }

            List<EntryGuideMovement> entryGuideMovementList = exitGuide.getInventoryMovementsList().stream().map( EntryGuideMovement::parseToDTO ).toList();
            params.put("ds",new JRBeanCollectionDataSource(entryGuideMovementList));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, new JREmptyDataSource());

            byte[] invoice = JasperExportManager.exportReportToPdf( jasperPrint );
            String sdf = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
            StringBuilder stringBuilder = new StringBuilder().append("entry-guide:");

            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(stringBuilder.append(exitGuide.getId()).append("generatedDate:").append(sdf).append(".pdf").toString())
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
            System.out.println("FILE ERROR: "+ e.getMessage() );
            return null;
        } catch (JRException e) {
            System.out.println("JASPER ERROR: "+ e.getMessage() );
            return null;
        }
    }

    public List<RejectionGuideDTO> getAllRejectionGuides(){
        return rejectionGuideRepository.findAll().stream().map(RejectionGuideDTO::parseToDTO).toList();
    }
}
