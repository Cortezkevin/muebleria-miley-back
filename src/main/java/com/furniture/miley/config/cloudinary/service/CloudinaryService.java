package com.furniture.miley.config.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.furniture.miley.config.cloudinary.dto.UploadDTO;
import com.furniture.miley.config.cloudinary.dto.UploadResultDTO;
import com.furniture.miley.config.cloudinary.utils.UploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${cloudinary.upload.preset}")
    private String uploadPreset;

    public void delete(String public_id) throws IOException {
        cloudinary.uploader().destroy(public_id, ObjectUtils.emptyMap());
    }

    public List<UploadResultDTO> uploadMany2(String folderName, List<UploadDTO> uploadDTOList) {
        try {
            List<UploadResultDTO> results = new ArrayList<>();
            for (UploadDTO dto : uploadDTOList) {
                log.info("FILE NAME: " + dto.file().getName() );
                String publicId = folderName + "/" + UploadUtils.formatFileName(dto.name());
                Map params = ObjectUtils.asMap(
                        "upload_preset", uploadPreset,
                        "public_id", publicId
                );

                Map res = cloudinary.uploader().upload(dto.file(), params);

                results.add(new UploadResultDTO( publicId , res.get("secure_url").toString())) ;
            }

            return results;
        }catch (IOException ex){
            log.error( ex.getMessage() );
            return null;
        }
    }

    public List<String> uploadManyAndGetUrls(String folderName, List<UploadDTO> uploadDTOList) {
        return this.uploadMany2(folderName, uploadDTOList).stream()
                .map(UploadResultDTO::url).toList();
    }

    public UploadResultDTO upload(String folderName, UploadDTO uploadDTO) throws IOException {
        String publicId = folderName + "/" + UploadUtils.formatFileName(uploadDTO.name());
        Map params = ObjectUtils.asMap(
                "upload_preset", uploadPreset,
                "public_id", publicId
        );
        Map res = cloudinary.uploader().upload(uploadDTO.file(), params);
        return new UploadResultDTO(publicId, res.get("secure_url").toString());
    }

    public String uploadAndGetUrl(String folderName, String fileName, File file) throws IOException {
        UploadDTO uploadDTO = new UploadDTO(file, fileName);
        UploadResultDTO uploadResultDTO = this.upload( folderName, uploadDTO );
        return uploadResultDTO.url();
    }
}
