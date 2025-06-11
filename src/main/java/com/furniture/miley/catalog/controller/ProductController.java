package com.furniture.miley.catalog.controller;

import com.furniture.miley.catalog.dto.color.ColorImagesDTO;
import com.furniture.miley.catalog.dto.product.CreateProductDTO;
import com.furniture.miley.catalog.dto.product.DetailedProductDTO;
import com.furniture.miley.catalog.dto.product.ProductDTO;
import com.furniture.miley.catalog.service.ProductService;
import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService mService;

    @GetMapping("/public")
    public ResponseEntity<SuccessResponseDTO<List<ProductDTO>>> getAll(){
        List<ProductDTO> productDTOList = mService.getAll();
        return productDTOList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        productDTOList
                )
        );
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<SuccessResponseDTO<DetailedProductDTO>> getDetailsById(
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

    @PostMapping( value = "/default-images", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } )
    public ResponseEntity<SuccessResponseDTO<ProductDTO>> createWithDefaultImages(
            @RequestPart("body") String bodyString,
            @RequestPart("files") List<MultipartFile> multipartFiles
    ){
        CreateProductDTO createProductDTO = UploadUtils.convertStringToObject( bodyString, CreateProductDTO.class );
        List<File> filesToUpload = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File fileToUpload = UploadUtils.getFileFromMultipartFile(multipartFile);
            filesToUpload.add(fileToUpload);
        }

        ProductDTO productDTO = mService.create(createProductDTO, filesToUpload);
        return ResponseEntity.created(URI.create("api/product/" + productDTO.id()))
                .body(
                        new SuccessResponseDTO<>(
                                ResponseMessage.SUCCESS,
                                HttpStatus.CREATED.name(),
                                productDTO
                        )
                );
    }

    @PostMapping( value = "/color_images", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE } )
    public ResponseEntity<SuccessResponseDTO<ProductDTO>> createWithColorsImages(
            @RequestPart("body") String bodyString,
            @RequestPart("files") List<MultipartFile> multipartFiles
    ){
        CreateProductDTO createProductDTO = UploadUtils.convertStringToObject( bodyString, CreateProductDTO.class );

        Map<String, List<File>> groupedColorFiles = new HashMap<>();
        for (ColorImagesDTO ci: createProductDTO.colorImages()){
            List<File> filesToUpload = new ArrayList<>();
            for (MultipartFile multipartFile : multipartFiles) {
                if(ci.fileNames().contains(multipartFile.getOriginalFilename())){
                    File fileToUpload = UploadUtils.getFileFromMultipartFile(multipartFile);
                    filesToUpload.add(fileToUpload);
                }
            }
            groupedColorFiles.put(ci.color(), filesToUpload);
        }
        ProductDTO productDTO = mService.create(createProductDTO, groupedColorFiles);
        return ResponseEntity.created(URI.create("api/product/" + productDTO.id()))
                .body(
                        new SuccessResponseDTO<>(
                                ResponseMessage.SUCCESS,
                                HttpStatus.CREATED.name(),
                                productDTO
                        )
                );
    }
}
