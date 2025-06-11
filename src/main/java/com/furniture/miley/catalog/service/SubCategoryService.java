package com.furniture.miley.catalog.service;

import com.furniture.miley.catalog.dto.subcategory.CreateSubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.DetailedSubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.SubCategoryDTO;
import com.furniture.miley.catalog.dto.subcategory.UpdateSubCategoryDTO;
import com.furniture.miley.catalog.model.Category;
import com.furniture.miley.catalog.model.SubCategory;
import com.furniture.miley.catalog.repository.SubCategoryRepository;
import com.furniture.miley.config.cloudinary.service.CloudinaryService;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository mRepository;

    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;

    public SubCategory findById(String id){
        return mRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resoruce"));
    }

    public List<SubCategoryDTO> getAll(){
        return mRepository.findAll().stream()
                .map( SubCategoryDTO::toDTO )
                .toList();
    }

    public SubCategoryDTO create(CreateSubCategoryDTO createSubCategoryDTO, File file) throws IOException {
        String id = UUID.randomUUID().toString();

        Category category = categoryService.findById(createSubCategoryDTO.category_id());
        SubCategory newSubCategory = SubCategory.builder()
                .id(id)
                .name(createSubCategoryDTO.name())
                .description(createSubCategoryDTO.description())
                .url_image(cloudinaryService.uploadAndGetUrl( "sub_category", id, file ))
                .category( category )
                .build();

        return SubCategoryDTO.toDTO( mRepository.save( newSubCategory ) );
    }

    public SubCategoryDTO update(UpdateSubCategoryDTO updateSubCategoryDTO, File file) throws IOException {
        SubCategory subCategory = this.findById( updateSubCategoryDTO.id() );
        if( file != null ){
            cloudinaryService.delete("sub_category"+"/"+ UploadUtils.formatFileName(subCategory.getName()));
            subCategory.setUrl_image( cloudinaryService.uploadAndGetUrl("sub_category", subCategory.getId(), file));
        }

        subCategory.setName(updateSubCategoryDTO.newName());
        subCategory.setDescription(updateSubCategoryDTO.newDescription());
        if(!Objects.equals(subCategory.getCategory().getId(), updateSubCategoryDTO.newCategoryId())){
            Category category = categoryService.findById( updateSubCategoryDTO.newCategoryId());
            subCategory.setCategory( category );
        }

        return SubCategoryDTO.toDTO( mRepository.save( subCategory ) );
    }

    public void delete(String id) throws IOException {
        SubCategory subCategory = this.findById( id );
        cloudinaryService.delete("sub_category/"+UploadUtils.formatFileName(subCategory.getName()));
        mRepository.delete(subCategory);
    }

    public DetailedSubCategoryDTO getDetailsById(String id) {
        return DetailedSubCategoryDTO.toDTO(this.findById(id));
    }
}
