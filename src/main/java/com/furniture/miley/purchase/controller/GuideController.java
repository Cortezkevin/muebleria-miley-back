package com.furniture.miley.purchase.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.purchase.dto.guide.*;
import com.furniture.miley.purchase.service.GuideService;
import com.furniture.miley.sales.dto.order.InvoiceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/guide")
@RequiredArgsConstructor
public class GuideController {

    private final GuideService guideService;

    @GetMapping("/entry")
    public ResponseEntity<SuccessResponseDTO<List<EntryGuideDTO>>> getAllEntryGuides(){
        List<EntryGuideDTO> entryGuideDTOList = guideService.getAllEntryGuides();
        return entryGuideDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        entryGuideDTOList
                )
        );
    }

    @GetMapping("/entry/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedEntryGuideDTO>> getEntryGuideById(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        guideService.getEntryGuideById( id )
                )
        );
    }

    @GetMapping("/entry/pdf/{id}")
    public ResponseEntity<Resource> exportEntryGuide(
            @PathVariable String id
    ){
        InvoiceDTO invoiceDTO = guideService.exportEntryGuide( id );
        return ResponseEntity.ok()
                .contentLength(invoiceDTO.invoiceLength().longValue())
                .contentType(MediaType.APPLICATION_PDF)
                .headers(invoiceDTO.headers())
                .body(new ByteArrayResource(invoiceDTO.resource()));
    }

    @GetMapping("/exit")
    public ResponseEntity<SuccessResponseDTO<List<ExitGuideDTO>>> getAllExitGuides(){
        List<ExitGuideDTO> exitGuideDTOList = guideService.getAllExitGuides();
        return exitGuideDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        exitGuideDTOList
                )
        );
    }

    @GetMapping("/exit/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedExitGuideDTO>> getExitGuideById(
            @PathVariable String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        guideService.getExitGuideById( id )
                )
        );
    }

    @GetMapping("/exit/pdf/{id}")
    public ResponseEntity<Resource> exportExitGuide(
            @PathVariable String id
    ){
        InvoiceDTO invoiceDTO = guideService.exportExitGuide( id );
        return ResponseEntity.ok()
                .contentLength(invoiceDTO.invoiceLength().longValue())
                .contentType(MediaType.APPLICATION_PDF)
                .headers(invoiceDTO.headers())
                .body(new ByteArrayResource(invoiceDTO.resource()));
    }

    @GetMapping("/rejection")
    public ResponseEntity<SuccessResponseDTO<List<RejectionGuideDTO>>> getAllRejectionGuides(){
        List<RejectionGuideDTO> rejectionGuideDTOList = guideService.getAllRejectionGuides();
        return rejectionGuideDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        rejectionGuideDTOList
                )
        );
    }
}
