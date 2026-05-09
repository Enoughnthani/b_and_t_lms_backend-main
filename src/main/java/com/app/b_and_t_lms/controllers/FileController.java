package com.app.b_and_t_lms.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/uploads")
public class FileController {

    @GetMapping("/content/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) throws IOException {
        Path path = Paths.get("uploads/content").resolve(filename);
        Resource resource = new UrlResource(path.toUri());
        
        // Check if file exists
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        
        // Method 1: Use Spring's MediaTypeFactory (recommended - covers most file types)
        String contentType = MediaTypeFactory.getMediaType(filename)
                .map(MediaType::toString)
                .orElseGet(() -> {
                    // Fallback: try Files.probeContentType
                    try {
                        return Files.probeContentType(path);
                    } catch (IOException e) {
                        return "application/octet-stream";
                    }
                });
        
        // If still null, use default
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline") // Display in browser if possible
                .body(resource);
    }
}