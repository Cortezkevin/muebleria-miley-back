package com.furniture.miley.catalog.controller;

import com.furniture.miley.catalog.dto.subcategory.CreateSubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.DetailedSubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.SubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.UpdateSubCategoryDTO;
import com.furniture.miley.catalog.service.SubCategoryService;
import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/sub-category")
@RequiredArgsConstructor
public class SubCategoryController {
    private final SubCategoryService mService;

    @GetMapping("/public")
    public ResponseEntity<SuccessResponseDTO<List<SubCategoryDTO>>> findAll(){
        List<SubCategoryDTO> categoryDTOList = mService.getAll();
        return categoryDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        categoryDTOList
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedSubCategoryDTO>> getDetailsById(
            @PathVariable("id") String id
    ) throws ResourceNotFoundException {
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        mService.getDetailsById(id)
                )
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<SubCategoryDTO>> create(
            @RequestPart("body") String bodyString,
            @RequestPart(name = "file", required = false) MultipartFile multipartFile
    ) throws IOException, ResourceNotFoundException {
        CreateSubCategoryDTO createSubCategoryDTO = UploadUtils.convertStringToObject(bodyString, CreateSubCategoryDTO.class);
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );

        SubCategoryDTO subCategoryDTO = mService.create(createSubCategoryDTO, fileToUpload);
        return ResponseEntity.created(URI.create("api/category/" + subCategoryDTO.id()))
                .body(
                        new SuccessResponseDTO<>(
                                ResponseMessage.CREATED,
                                HttpStatus.CREATED.name(),
                                subCategoryDTO
                        )
                );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<SubCategoryDTO>> update(
            @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @RequestPart("body") String bodyString
    ) throws IOException, ResourceNotFoundException {
        UpdateSubCategoryDTO updateSubCategoryDTO = UploadUtils.convertStringToObject( bodyString, UpdateSubCategoryDTO.class );
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        mService.update(updateSubCategoryDTO, fileToUpload)
                )
        );
    }
}
