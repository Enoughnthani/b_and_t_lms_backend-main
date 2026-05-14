package com.app.b_and_t_lms.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileController {

    private static final String ASSESSMENTS_DIR = "C:/uploads/assessments/";
    private static final String SUBMISSIONS_DIR = "C:/uploads/submissions/";
    private static final String CONTENT_DIR = "C:/uploads/content/";
    private static final String PROGRAMS_DIR = "C:/uploads/programs/";

    // For /uploads/assessments/ path
    @GetMapping("/uploads/assessments/{filename}")
    public ResponseEntity<Resource> getAssessmentFile(@PathVariable String filename) throws IOException {
        return serveFile(ASSESSMENTS_DIR, filename, "inline", null);
    }

    @GetMapping("/uploads/assessments/{filename}/download")
    public ResponseEntity<Resource> downloadAssessmentFile(
            @PathVariable String filename,
            @RequestParam(required = false) String originalName) throws IOException {

        String decodedName = null;
        if (originalName != null && !originalName.isEmpty()) {
            decodedName = URLDecoder.decode(originalName, StandardCharsets.UTF_8.toString());
        }
        return serveFile(ASSESSMENTS_DIR, filename, "attachment", decodedName);
    }

    // For /uploads/submissions/ path
    @GetMapping("/uploads/submissions/{filename}")
    public ResponseEntity<Resource> getSubmissionFile(@PathVariable String filename) throws IOException {
        return serveFile(SUBMISSIONS_DIR, filename, "inline", null);
    }

    @GetMapping("/uploads/submissions/{filename}/download")
    public ResponseEntity<Resource> downloadSubmissionFile(
            @PathVariable String filename,
            @RequestParam(required = false) String originalName) throws IOException {

        String decodedName = null;
        if (originalName != null && !originalName.isEmpty()) {
            decodedName = URLDecoder.decode(originalName, StandardCharsets.UTF_8.toString());
        }
        return serveFile(SUBMISSIONS_DIR, filename, "attachment", decodedName);
    }

    // For /uploads/content/ path
    @GetMapping("/uploads/content/{filename}")
    public ResponseEntity<Resource> getContentFile(@PathVariable String filename) throws IOException {
        return serveFile(CONTENT_DIR, filename, "inline", null);
    }

    @GetMapping("/uploads/content/{filename}/download")
    public ResponseEntity<Resource> downloadContentFile(
            @PathVariable String filename,
            @RequestParam(required = false) String originalName) throws IOException {

        String decodedName = null;
        if (originalName != null && !originalName.isEmpty()) {
            decodedName = URLDecoder.decode(originalName, StandardCharsets.UTF_8.toString());
        }
        return serveFile(CONTENT_DIR, filename, "attachment", decodedName);
    }

    @GetMapping("/uploads/programs/{filename}")
    public ResponseEntity<Resource> getProgramFile(@PathVariable String filename) throws IOException {
        return serveFile(PROGRAMS_DIR, filename, "inline", null);
    }

    @GetMapping("/uploads/programs/{filename}/download")
    public ResponseEntity<Resource> downloadProgramFile(
            @PathVariable String filename,
            @RequestParam(required = false) String originalName) throws IOException {

        String decodedName = null;
        if (originalName != null && !originalName.isEmpty()) {
            decodedName = URLDecoder.decode(originalName, StandardCharsets.UTF_8.toString());
        }
        return serveFile(PROGRAMS_DIR, filename, "attachment", decodedName);
    }

    private ResponseEntity<Resource> serveFile(String dir, String filename, String disposition, String originalName)
            throws IOException {
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

     
        String downloadFileName;
        if ("attachment".equals(disposition) && originalName != null && !originalName.isEmpty()) {
            downloadFileName = originalName;
        } else {
            downloadFileName = resource.getFilename();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + downloadFileName + "\"")
                .body(resource);
    }
}