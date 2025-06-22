package com.furniture.miley.catalog.controller;

import com.furniture.miley.catalog.dto.category.CategoryDTO;
import com.furniture.miley.catalog.dto.category.CreateCategoryDTO;
import com.furniture.miley.catalog.dto.category.DetailedCategoryDTO;
import com.furniture.miley.catalog.dto.category.UpdateCategoryDTO;
import com.furniture.miley.catalog.service.CategoryService;
import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService mService;

    @GetMapping("/public")
    public ResponseEntity<SuccessResponseDTO<List<CategoryDTO>>> findAll(){
        List<CategoryDTO> categoryDTOList = mService.getAll();
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
    public ResponseEntity<SuccessResponseDTO<DetailedCategoryDTO>> getDetailsById(
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
    public ResponseEntity<SuccessResponseDTO<CategoryDTO>> create(
            @RequestPart("body") String bodyString,
             @RequestPart("file") MultipartFile multipartFile
            ) throws IOException {
        CreateCategoryDTO createCategoryDTO = UploadUtils.convertStringToObject(bodyString, CreateCategoryDTO.class);
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );

        CategoryDTO categoryDTO = mService.create(createCategoryDTO, fileToUpload);
        return ResponseEntity.created(URI.create("api/category/" + categoryDTO.id()))
                .body(
                        new SuccessResponseDTO<>(
                                ResponseMessage.CREATED,
                                HttpStatus.CREATED.name(),
                                categoryDTO
                        )
                );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<CategoryDTO>> update(
            @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @RequestPart("body") String bodyString
    ) throws IOException, ResourceNotFoundException {
        UpdateCategoryDTO updateCategoryDTO = UploadUtils.convertStringToObject( bodyString, UpdateCategoryDTO.class );
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.UPDATED,
                        HttpStatus.OK.name(),
                        mService.update(updateCategoryDTO, fileToUpload)
                )
        );
    }
}