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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private static final String CONTENT_DIR = "C:/uploads/content/";
    private static final String PROGRAMS_DIR = "C:/uploads/programs/";

    // Preview file (opens in browser)
    @GetMapping("/content/{filename}")
    public ResponseEntity<Resource> getContentFile(@PathVariable String filename) throws IOException {
        return serveFile(CONTENT_DIR, filename, "inline");
    }

    // Download file (forces download)
    @GetMapping("/content/{filename}/download")
    public ResponseEntity<Resource> downloadContentFile(@PathVariable String filename) throws IOException {
        return serveFile(CONTENT_DIR, filename, "attachment");
    }

    @GetMapping("/programs/{filename}")
    public ResponseEntity<Resource> getProgramFile(@PathVariable String filename) throws IOException {
        return serveFile(PROGRAMS_DIR, filename, "inline");
    }

    @GetMapping("/programs/{filename}/download")
    public ResponseEntity<Resource> downloadProgramFile(@PathVariable String filename) throws IOException {
        return serveFile(PROGRAMS_DIR, filename, "attachment");
    }

    private ResponseEntity<Resource> serveFile(String dir, String filename, String disposition) throws IOException {
        Path path = Paths.get(dir).resolve(filename).normalize();
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = MediaTypeFactory.getMediaType(filename)
                .map(MediaType::toString)
                .orElseGet(() -> {
                    try {
                        return Files.probeContentType(path);
                    } catch (IOException e) {
                        return "application/octet-stream";
                    }
                });

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}