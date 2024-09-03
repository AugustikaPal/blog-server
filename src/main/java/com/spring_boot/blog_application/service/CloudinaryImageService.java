package com.spring_boot.blog_application.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryImageService {

    private final Cloudinary cloudinary;

    public CloudinaryImageService(@Value("${cloudinary.cloud_name}") String cloudName,
                              @Value("${cloudinary.api_key}") String apiKey,
                              @Value("${cloudinary.api_secret}") String apiSecret) {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }


    private String extractPublicId(String imageUrl) {
        // Assuming the URL structure is something like this:
        // https://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<public_id>.<format>

        String[] parts = imageUrl.split("/");
        int index = -1;

        // Locate the "upload" part of the URL and then get the public ID
        for (int i = 0; i < parts.length; i++) {
            if ("upload".equals(parts[i])) {
                index = i + 1;
                break;
            }
        }

        if (index != -1 && index < parts.length) {
            // Extract the public ID, assuming it has a format like "folder/sample.jpg"
            String publicIdWithExtension = String.join("/", Arrays.copyOfRange(parts, index, parts.length));
            int dotIndex = publicIdWithExtension.lastIndexOf('.');
            if (dotIndex != -1) {
                return publicIdWithExtension.substring(0, dotIndex); // public ID without extension
            }
            return publicIdWithExtension; // in case there's no extension
        }

        throw new IllegalArgumentException("Invalid Cloudinary URL format.");
    }
//    private String extractPublicId(String imageUrl) {
//        // URL structure might vary; adjust the split logic based on your URL format
//        String[] parts = imageUrl.split("/");
//
//        // Assuming the public ID is the segment before the file extension
//        // Example URL: https://res.cloudinary.com/demo/image/upload/v1615124541/sample.jpg
//        // The public ID is 'sample'
//
//        // Find the index of 'upload' in the URL
//        int uploadIndex = -1;
//        for (int i = 0; i < parts.length; i++) {
//            if ("upload".equals(parts[i])) {
//                uploadIndex = i;
//                break;
//            }
//        }
//
//        // Extract public ID from the segment after 'upload'
//        if (uploadIndex >= 0 && uploadIndex + 1 < parts.length) {
//            // Extract the segment with the public ID
//            String publicIdWithExtension = parts[uploadIndex + 1];
//
//            // Remove the file extension to get the public ID
//            int dotIndex = publicIdWithExtension.lastIndexOf('.');
//            if (dotIndex >= 0) {
//                return publicIdWithExtension.substring(0, dotIndex);
//            }
//
//            return publicIdWithExtension; // Return as is if no extension found
//        }
//
//        throw new IllegalArgumentException("Invalid Cloudinary URL format.");
//    }


    public boolean deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String resultStatus = (String) result.get("result");
            if ("ok".equals(resultStatus)) {
                return true;
            } else {
                System.err.println("Cloudinary delete result: " + resultStatus);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//    public void deleteImage(String imageUrl) {
//        try {
//            // Extract the public ID from the image URL
//            String publicId = extractPublicId(imageUrl);
//
//            // Use Cloudinary's destroy method to delete the image
//            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//
//            // Check if deletion was successful
//            if (!"ok".equals(result.get("result"))) {
//                throw new RuntimeException("Failed to delete image from Cloudinary.");
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error occurred while deleting image from Cloudinary: " + e.getMessage(), e);
//        }
//    }


//    public void deleteImage(String imageUrl) {
//        // Extract public ID from the URL if necessary
//        String publicId = extractPublicId(imageUrl);
//        try {
//            // Log before the deletion
//            System.out.println("Deleting from Cloudinary: " + publicId);
//
//            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//
//            // Log after the deletion
//            System.out.println("Deleted from Cloudinary: " + publicId);
//        } catch (IOException e) {
//            // Log any exception
//            System.out.println("Error deleting from Cloudinary: " + e.getMessage());
//            throw new RuntimeException("Failed to delete image from Cloudinary");
//        }
//    }

    public List<String> uploadImages(MultipartFile[] files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                imageUrls.add((String) uploadResult.get("secure_url"));
            }
        }
        return imageUrls;
    }
}

