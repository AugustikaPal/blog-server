package com.spring_boot.blog_application.controller;

import com.spring_boot.blog_application.service.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = {"https://blog-client-60xvx45w8-augustika1524gmailcoms-projects.vercel.app", "http://localhost:5500"})
@RequestMapping("/api/v1/images")
public class ImageUploadController {

    @Autowired
    private CloudinaryImageService cloudinaryImageService;

    @PostMapping("/upload")
    public ResponseEntity<List<String>> uploadImage(@RequestParam("file") MultipartFile[] file) {
        try {
            List<String> imageUrls = cloudinaryImageService.uploadImages(file);
            return ResponseEntity.ok(imageUrls);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Collections.singletonList("Image upload failed: " + e.getMessage()));
        }
    }
}

