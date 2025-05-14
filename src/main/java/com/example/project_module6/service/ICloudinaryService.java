package com.example.project_module6.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadImage(MultipartFile file) throws IOException;
}
