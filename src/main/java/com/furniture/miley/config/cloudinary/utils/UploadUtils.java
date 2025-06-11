package com.furniture.miley.config.cloudinary.utils;

import com.google.gson.Gson;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class UploadUtils {
    public static File getFileFromMultipartFile(MultipartFile multipartFile) {
        if (multipartFile != null) {
            File f = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
            try {
                multipartFile.transferTo(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        }
        return null;
    }

    public static <T> T convertStringToObject(String stringObj, Class<T> objClass) {
        return new Gson().fromJson(stringObj, objClass);
    }

    public static String formatFileName(String fileName) {
        return fileName.toLowerCase().replaceAll("/", "-").replaceAll(" ", "_");
    }
}