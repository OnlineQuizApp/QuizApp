package com.example.project_module6.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dhjd8skq5",
                "api_key", "734173533551694",
                "api_secret", "0PhemI9szwWxBaXs9DTksJeF24Y",
                "secure", true
        ));
    }
}
