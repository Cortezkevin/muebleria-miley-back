package com.furniture.miley.catalog.service;

import com.furniture.miley.catalog.dto.category.CategoryDTO;
import com.furniture.miley.catalog.dto.category.CreateCategoryDTO;
import com.furniture.miley.catalog.dto.category.DetailedCategoryDTO;
import com.furniture.miley.catalog.dto.category.UpdateCategoryDTO;
import com.furniture.miley.catalog.model.Category;
import com.furniture.miley.catalog.repository.CategoryRepository;
import com.furniture.miley.config.cloudinary.dto.UploadDTO;
import com.furniture.miley.config.cloudinary.service.CloudinaryService;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository mRepository;

    private final CloudinaryService cloudinaryService;

    public Category findById(String id){
        return mRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource"));
    }

    public List<CategoryDTO> getAll(){
        return mRepository.findAll()
                .stream().map(CategoryDTO::toDTO)
                .toList();
    }

    public DetailedCategoryDTO getDetailsById(String id){
        return DetailedCategoryDTO.toDTO(this.findById(id));
    }

    public CategoryDTO create(CreateCategoryDTO createCategoryDTO, File file) throws IOException {
        String id = UUID.randomUUID().toString();

        Category newCategory = Category.builder()
                .id(id)
                .name( createCategoryDTO.name() )
                .description( createCategoryDTO.description() )
                .url_image( cloudinaryService.uploadAndGetUrl("category", id, file) )
                .build();

        return CategoryDTO.toDTO( mRepository.save( newCategory ) );
    }

    public CategoryDTO update(UpdateCategoryDTO updateCategoryDTO, File file) throws IOException {
        Category category = this.findById( updateCategoryDTO.id() );

        if( file != null ){
            UploadDTO uploadDTO = new UploadDTO(file, category.getId());
            cloudinaryService.delete("category"+"/"+ UploadUtils.formatFileName(category.getName()));
            category.setUrl_image(cloudinaryService.upload("category", uploadDTO).url());
        }

        category.setName(updateCategoryDTO.newName());
        category.setDescription(updateCategoryDTO.newDescription());
        return CategoryDTO.toDTO( mRepository.save( category ) );
    }

    public void delete(String id) throws IOException {
        Category category = this.findById( id );
        cloudinaryService.delete("category/"+UploadUtils.formatFileName(category.getName()));
        mRepository.delete(category);
    }
}
