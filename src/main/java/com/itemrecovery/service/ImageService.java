package com.itemrecovery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for handling image upload operations.
 * Manages saving images to the file system and returning file paths.
 */
@Service
public class ImageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Save an uploaded image file for a lost item.
     * @param file the uploaded file
     * @return the relative path to the saved image
     * @throws IOException if file cannot be saved
     */
    public String saveLostItemImage(MultipartFile file) throws IOException {
        return saveImage(file, "lost");
    }

    /**
     * Save an uploaded image file for a found item.
     * @param file the uploaded file
     * @return the relative path to the saved image
     * @throws IOException if file cannot be saved
     */
    public String saveFoundItemImage(MultipartFile file) throws IOException {
        return saveImage(file, "found");
    }

    /**
     * Internal method to save an image file.
     * @param file the uploaded file
     * @param subdirectory the subdirectory (lost or found)
     * @return the relative path to the saved image
     * @throws IOException if file cannot be saved
     */
    private String saveImage(MultipartFile file, String subdirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Validate file type
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Filename is null");
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // Generate unique filename
        String filename = UUID.randomUUID().toString() + extension;
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, subdirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path
        return uploadDir + "/" + subdirectory + "/" + filename;
    }

    /**
     * Delete an image file from the file system.
     * @param imagePath the path to the image file
     * @throws IOException if file cannot be deleted
     */
    public void deleteImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(imagePath);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
