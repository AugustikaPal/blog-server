package com.spring_boot.blog_application.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = null;
        try {
            config = ObjectUtils.asMap(
                    "cloud_name", "<YOUR_CLOUD_NAME>",
                    "api_key", "<YOUR_API_KEY>",
                    "api_secret", "<YOUR_API_SECRET>");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Cloudinary(config);
    }
}
