package com.app.b_and_t_lms.services;

import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final String BASE_PATH = "C:/uploads/";

    public String saveImage(String base64Image, String folder) {
        try {
            Path uploadDir = Paths.get(BASE_PATH + folder);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String imageString = base64Image.contains(",")
                    ? base64Image.split(",")[1]
                    : base64Image;

            byte[] imageBytes = Base64.getDecoder().decode(imageString);
            String fileName = UUID.randomUUID().toString() + ".jpg";

            Files.write(uploadDir.resolve(fileName), imageBytes);

            return "/uploads/" + folder + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save image: " );
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isBlank()) {
                return;
            }

            String relativePath = imageUrl.replace("/uploads/", "");

            Path filePath = Paths.get(BASE_PATH + relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image: " );
        }
    }
}