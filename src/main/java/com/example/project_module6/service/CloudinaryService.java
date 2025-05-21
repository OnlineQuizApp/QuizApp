package com.example.project_module6.service;

import com.cloudinary.Cloudinary;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService implements ICloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    @Override
    public String uploadVideo(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "video"
        ));
        // Lấy URL video trực tiếp từ Cloudinary trả về
        return uploadResult.get("secure_url").toString();
    }
}
