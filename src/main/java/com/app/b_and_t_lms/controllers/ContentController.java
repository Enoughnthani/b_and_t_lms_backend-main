package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.dto.ContentRequestDTO;
import com.app.b_and_t_lms.dto.ContentResponseDTO;
import com.app.b_and_t_lms.models.Content.ContentType;
import com.app.b_and_t_lms.services.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACILITATOR')")
public class ContentController {

    private final ContentService contentService;

    // Get root content for a unit standard
    @GetMapping("/unit-standard/{unitStandardId}")
    public ResponseEntity<List<ContentResponseDTO>> getUnitStandardRootContent(@PathVariable Long unitStandardId) {
        List<ContentResponseDTO> contents = contentService.getUnitStandardRootContent(unitStandardId);
        return ResponseEntity.ok(contents);
    }

    // Get children for a unit standard folder
    @GetMapping("/unit-standard/{unitStandardId}/children/{parentId}")
    public ResponseEntity<List<ContentResponseDTO>> getUnitStandardChildren(
            @PathVariable Long unitStandardId,
            @PathVariable Long parentId) {
        List<ContentResponseDTO> contents = contentService.getUnitStandardChildren(parentId, unitStandardId);
        return ResponseEntity.ok(contents);
    }

    // Get children of any folder
    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<ContentResponseDTO>> getChildren(@PathVariable Long parentId) {
        List<ContentResponseDTO> contents = contentService.getChildren(parentId);
        return ResponseEntity.ok(contents);
    }

    // Create content (folder or link)
    @PostMapping
    public ResponseEntity<ContentResponseDTO> createContent(@RequestBody ContentRequestDTO dto) {
        ContentResponseDTO created = contentService.createContent(dto);
        return ResponseEntity.ok(created);
    }

    // Upload file
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("unitStandardId") Long unitStandardId,
            @RequestParam(value = "parentId", required = false) Long parentId) throws IOException {

        ContentType contentType = ContentType.valueOf(type.toUpperCase());
        ContentResponseDTO created = contentService.uploadFile(file, name, contentType, unitStandardId, parentId);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> updateContent(@PathVariable Long id, @RequestBody ContentRequestDTO dto) {
        ContentResponseDTO updated = contentService.updateContent(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}