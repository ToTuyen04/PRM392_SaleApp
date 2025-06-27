package com.salesapp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload image to Cloudinary
     * @param file MultipartFile to upload
     * @return URL of uploaded image
     */
    public String uploadImage(MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Check if file is image
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }

            // Upload to Cloudinary
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "products", // Store in products folder
                    "resource_type", "image",
                    "quality", "auto:good",
                    "fetch_format", "auto"
                )
            );

            // Return the secure URL
            String imageUrl = (String) uploadResult.get("secure_url");
            log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    /**
     * Delete image from Cloudinary
     * @param imageUrl URL of image to delete
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Image deleted: {} - Result: {}", publicId, result.get("result"));
            }
        } catch (Exception e) {
            log.error("Error deleting image from Cloudinary: {}", imageUrl, e);
        }
    }

    /**
     * Extract public_id from Cloudinary URL
     * @param imageUrl Cloudinary image URL
     * @return public_id or null if cannot extract
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
                return null;
            }

            // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
            String[] parts = imageUrl.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension
                int dotIndex = lastPart.lastIndexOf('.');
                if (dotIndex > 0) {
                    String publicId = lastPart.substring(0, dotIndex);
                    return "products/" + publicId; // Include folder prefix
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting public_id from URL: {}", imageUrl, e);
            return null;
        }
    }
}
