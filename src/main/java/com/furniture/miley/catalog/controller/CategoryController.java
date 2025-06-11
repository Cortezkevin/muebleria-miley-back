package com.furniture.miley.catalog.controller;

import com.furniture.miley.catalog.dto.category.CategoryDTO;
import com.furniture.miley.catalog.dto.category.CreateCategoryDTO;
import com.furniture.miley.catalog.dto.category.DetailedCategoryDTO;
import com.furniture.miley.catalog.dto.category.UpdateCategoryDTO;
import com.furniture.miley.catalog.service.CategoryService;
import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService mService;

    @GetMapping
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
    ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        mService.getDetailsById(id)
                )
        );
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<CategoryDTO>> create(
            @RequestPart("createCategoryDTO") String createCategoryDTOString,
             @RequestPart(name = "file", required = false) MultipartFile multipartFile
            ) throws IOException {
        CreateCategoryDTO createCategoryDTO = UploadUtils.convertStringToObject(createCategoryDTOString, CreateCategoryDTO.class);
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );

        CategoryDTO categoryDTO = mService.create(createCategoryDTO, fileToUpload);
        return ResponseEntity.created(URI.create("api/category/" + categoryDTO.id()))
                .body(
                        new SuccessResponseDTO<>(
                                ResponseMessage.SUCCESS,
                                HttpStatus.CREATED.name(),
                                categoryDTO
                        )
                );
    }

    @PutMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<SuccessResponseDTO<CategoryDTO>> update(
            @RequestPart(name = "file", required = false) MultipartFile multipartFile,
            @RequestPart("updateCategoryDTO") String updateCategoryDTOString
    ) throws IOException {
        UpdateCategoryDTO updateCategoryDTO = UploadUtils.convertStringToObject( updateCategoryDTOString, UpdateCategoryDTO.class );
        File fileToUpload = UploadUtils.getFileFromMultipartFile( multipartFile );
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        mService.update(updateCategoryDTO, fileToUpload)
                )
        );
    }
}