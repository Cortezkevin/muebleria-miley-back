package com.furniture.miley.catalog.service;

import com.furniture.miley.catalog.dto.color.ProductColorDTO;
import com.furniture.miley.catalog.dto.feature.ProductFeatureDTO;
import com.furniture.miley.catalog.dto.image.ProductImageDTO;
import com.furniture.miley.catalog.dto.product.CreateProductDTO;
import com.furniture.miley.catalog.dto.product.DetailedProductDTO;
import com.furniture.miley.catalog.dto.product.ProductDTO;
import com.furniture.miley.catalog.enums.AcquisitionType;
import com.furniture.miley.catalog.model.Product;
import com.furniture.miley.catalog.model.SubCategory;
import com.furniture.miley.catalog.model.color.Color;
import com.furniture.miley.catalog.model.color.ProductColor;
import com.furniture.miley.catalog.model.feature.Feature;
import com.furniture.miley.catalog.model.feature.ProductFeature;
import com.furniture.miley.catalog.model.image.ColorProductImage;
import com.furniture.miley.catalog.model.image.DefaultProductImage;
import com.furniture.miley.catalog.model.image.ProductImage;
import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.catalog.repository.color.ProductColorRepository;
import com.furniture.miley.config.cloudinary.dto.UploadDTO;
import com.furniture.miley.config.cloudinary.dto.UploadResultDTO;
import com.furniture.miley.config.cloudinary.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductColorRepository productColorRepository;

    private final ProductRepository mRepository;

    private final ColorService colorService;
    private final FeatureService featureService;
    private final SubCategoryService subCategoryService;
    private final CloudinaryService cloudinaryService;

    public List<ProductDTO> getAll(){
        return mRepository.findAll()
                .stream().map(ProductDTO::toDTO)
                .toList();
    }

    public Product findById(String id){ // metodo separado exclusivo para retornar la entidad, con mayor control sobre los errores y otros detalles
        return mRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource exception"));
    }

    public DetailedProductDTO getDetailsById(String id){
        return DetailedProductDTO.toDTO(this.findById(id));
    }

    private Product saveImagesInProduct(Product product, List<ProductImageDTO> images){
        try {
            images.forEach( imageDTO -> {
                product.getImages().add(new DefaultProductImage(
                        imageDTO.url(),
                        imageDTO.imageId(),
                        product
                ));
            });
            return mRepository.save(product);
        } catch (Exception e) {
            log.error("Error in saveImagesInProduct: {}", e.getMessage());
            return product;
        }
    }

    private Product saveColorsWithImagesInProduct(Product product, List<ProductColorDTO> colorsWithImages){
        try {
            colorsWithImages.forEach(colorWithImages -> {
                Color color = colorService.findOrCreateByColor(colorWithImages.color());
                ProductColor productColor = ProductColor.builder()
                        .color(color)
                        .product(product)
                        .build();

                colorWithImages.images().forEach(colorImage -> {
                    productColor.getImages().add(new ColorProductImage(
                            colorImage.url(),
                            colorImage.imageId(),
                            productColor
                    ));
                });

                product.getColors().add(productColor);
            });
            return mRepository.save(product);
        }catch (Exception e){
            log.error("Error in saveColorsWithImagesInProduct: {}", e.getMessage());
            return product;
        }
    }

    private Product saveFeaturesInProduct(Product product, List<ProductFeatureDTO> features){
        try {
            features.forEach(featuresWithValues -> {
                Feature feature = featureService.findOrCreate(featuresWithValues.feature());
                ProductFeature productFeature = ProductFeature.builder()
                        .feature(feature)
                        .product(product)
                        .value(featuresWithValues.value())
                        .build();
                product.getFeatures().add(productFeature);
            });
            return mRepository.save(product);
        }catch (Exception e){
            log.error("Error in saveFeaturesInProduct: {}", e.getMessage());
            return product;
        }
    }

    public ProductDTO create(CreateProductDTO createProductDTO, List<File> files){
        SubCategory subCategory = subCategoryService.findById(createProductDTO.subcategoryId());

        Product productToCreate = Product.builder()
                .name(createProductDTO.name())
                .price(createProductDTO.price())
                .subCategory(subCategory)
                .adAcquisitionType(AcquisitionType.MANUFACTURED)
                .build();

        Product newProduct = mRepository.save(productToCreate);

        List<UploadDTO> uploadDTOList = new ArrayList<>();

        for (int i = 0; i < files.size(); i ++ ){
            File file = files.get(i);
            uploadDTOList.add( new UploadDTO( file, newProduct.getId() + "_" + (i+1) ));
        }

        List<ProductImageDTO> images = cloudinaryService.uploadMany2( "product", uploadDTOList )
                .stream().map(uploadResultDTO -> new ProductImageDTO(uploadResultDTO.url(), uploadResultDTO.public_id()))
                .toList();

        newProduct = this.saveFeaturesInProduct(newProduct, createProductDTO.features());
        newProduct = this.saveImagesInProduct(newProduct, images);

        return ProductDTO.toDTO( mRepository.save(newProduct) );
    }

    public ProductDTO create(CreateProductDTO createProductDTO, Map<String, List<File>> colorFiles){
        SubCategory subCategory = subCategoryService.findById(createProductDTO.subcategoryId());

        Product productToCreate = Product.builder()
                .name(createProductDTO.name())
                .price(createProductDTO.price())
                .subCategory(subCategory)
                .adAcquisitionType(AcquisitionType.MANUFACTURED)
                .build();

        Product newProduct = mRepository.save(productToCreate);
        List<UploadDTO> uploadDTOList = new ArrayList<>();

        Map<String, List<ProductImageDTO>> colorsWithImagesUrls = new HashMap<>();
        for (Map.Entry<String, List<File>> entry : colorFiles.entrySet()) {
            String color = entry.getKey();
            List<File> files = entry.getValue();

            for (int i = 0; i < files.size(); i ++ ){
                File file = files.get(i);
                uploadDTOList.add( new UploadDTO( file, newProduct.getId() + "_" + (i+1) ));
            }

            List<ProductImageDTO> images = cloudinaryService.uploadMany2( "product", uploadDTOList )
                    .stream().map(uploadResultDTO -> new ProductImageDTO(uploadResultDTO.url(), uploadResultDTO.public_id()))
                    .toList();

            colorsWithImagesUrls.put(color, images);
            uploadDTOList.clear();
        }

        List<ProductColorDTO> colors = colorsWithImagesUrls.entrySet()
                .stream()
                .map(entry -> new ProductColorDTO(entry.getKey(), entry.getValue()))
                .toList();


        newProduct = this.saveFeaturesInProduct(newProduct, createProductDTO.features());
        newProduct = this.saveColorsWithImagesInProduct(newProduct, colors);

        return ProductDTO.toDTO( mRepository.save(newProduct) );
    }

    public List<String> findImagesByProductColor(String productId, String color) {
        Color colorFound = colorService.findOrCreateByColor(color);
        Product productFound = mRepository.findById(productId).orElse(null);

        ProductColor productColor = productColorRepository.findByProductAndColor(productFound, colorFound).get();
        return productColor.getImages().stream().map(ProductImage::getUrl).toList();
    }
}
