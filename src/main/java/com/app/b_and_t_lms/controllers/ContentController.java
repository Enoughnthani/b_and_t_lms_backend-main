package com.app.b_and_t_lms.controllers;

import com.app.b_and_t_lms.dto.ContentRequestDTO;
import com.app.b_and_t_lms.dto.ContentResponseDTO;
import com.app.b_and_t_lms.models.Content.ContentType;
import com.app.b_and_t_lms.services.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @GetMapping("/program/{programId}")
    public List<ContentResponseDTO> getRootContent(@PathVariable Long programId) {
        return contentService.getRootContent(programId);
    }

    @GetMapping("/children/{parentId}")
    public List<ContentResponseDTO> getChildren(@PathVariable Long parentId) {
        return contentService.getChildren(parentId);
    }

    @PostMapping
    public ContentResponseDTO createContent(@RequestBody ContentRequestDTO dto) {
        return contentService.createContent(dto);
    }

    // Upload file (multipart form data)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ContentResponseDTO uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("programId") Long programId,
            @RequestParam(value = "unitStandardId", required = false) Long unitStandardId,
            @RequestParam(value = "parentId", required = false) Long parentId) throws IOException {

        ContentType contentType = ContentType.valueOf(type.toUpperCase());
        return contentService.uploadFile(file, name, contentType, programId, unitStandardId, parentId);
    }

    @PutMapping("/{id}")
    public ContentResponseDTO updateContent(@PathVariable Long id, @RequestBody ContentRequestDTO dto) {
        return contentService.updateContent(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
    }
}