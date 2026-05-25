package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.dto.ContentRequestDTO;
import com.app.b_and_t_lms.dto.ContentResponseDTO;
import com.app.b_and_t_lms.models.Content;
import com.app.b_and_t_lms.models.Content.ContentType;
import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.repositories.ContentRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final UnitStandardRepository unitStandardRepository;

    private final String UPLOAD_DIR = "C:/uploads/content/";

   
    public List<ContentResponseDTO> getUnitStandardRootContent(Long unitStandardId) {
        unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));

        return contentRepository.findRootContentByUnitStandardId(unitStandardId)
                .stream()
                .map(ContentResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDTO> getUnitStandardChildren(Long parentId, Long unitStandardId) {
        return contentRepository.findByParentIdAndUnitStandardId(parentId, unitStandardId)
                .stream()
                .map(ContentResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<ContentResponseDTO> getChildren(Long parentId) {
        return contentRepository.findByParentId(parentId)
                .stream()
                .map(ContentResponseDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public ContentResponseDTO createContent(ContentRequestDTO dto) {
        Content content = new Content();
        content.setName(dto.getName());
        content.setType(dto.getType());

        if (dto.getUnitStandardId() == null) {
            throw new RuntimeException("Unit Standard ID is required");
        }

        UnitStandard unitStandard = unitStandardRepository.findById(dto.getUnitStandardId())
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + dto.getUnitStandardId()));
        content.setUnitStandard(unitStandard);

        if (dto.getParentId() != null) {
            Content parent = contentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent folder not found with id: " + dto.getParentId()));
            content.setParent(parent);
        }


        if (dto.getType() == ContentType.LINK) {
            content.setExternalUrl(dto.getExternalUrl());
        }

        if (dto.getFileUrl() != null) {
            content.setFileUrl(dto.getFileUrl());
        }

        if (dto.getFileSize() != null) {
            content.setFileSize(dto.getFileSize());
        }

        content.setDownloadable(dto.getDownloadable() != null ? dto.getDownloadable() : true);

        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    @Transactional
    public ContentResponseDTO uploadFile(MultipartFile file, String name, ContentType type,
            Long unitStandardId, Long parentId) throws IOException {

        // Validate file size (optional)
        long maxFileSize = 500 * 1024 * 1024; // 500MB
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed size of 500MB");
        }

        // Validate video file type
        if (type == ContentType.VIDEO) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("video/")) {
                throw new RuntimeException("Invalid video file type. Please upload a valid video file.");
            }
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Save file with better error handling
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " );
        }

        // Get file size in readable format
        String fileSize = formatFileSize(file.getSize());

        // Create content entity
        Content content = new Content();
        content.setName(name);
        content.setType(type);
        content.setFileUrl("/uploads/content/" + filename);
        content.setFileSize(fileSize);
        content.setDownloadable(true);

        // Set unit standard (required)
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));
        content.setUnitStandard(unitStandard);

        // Set parent folder (optional)
        if (parentId != null) {
            Content parent = contentRepository.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Parent folder not found with id: " + parentId));
            content.setParent(parent);
        }

        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    // Update content
    @Transactional
    public ContentResponseDTO updateContent(Long id, ContentRequestDTO dto) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));

        if (dto.getName() != null) {
            content.setName(dto.getName());
        }

        if (dto.getExternalUrl() != null) {
            content.setExternalUrl(dto.getExternalUrl());
        }

        if (dto.getFileUrl() != null) {
            content.setFileUrl(dto.getFileUrl());
        }

        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    @Transactional
    public ContentResponseDTO moveToUnitStandard(Long contentId, Long unitStandardId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));
        content.setUnitStandard(unitStandard);
        content.setParent(null);

        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    private String formatFileSize(long size) {
        if (size < 1024)
            return size + " B";
        if (size < 1024 * 1024)
            return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024)
            return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    @Transactional
    public void bulkDeleteContents(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<Content> contents = contentRepository.findAllById(ids);
        for (Content content : contents) {
            if (content.getType() != ContentType.FOLDER && content.getFileUrl() != null) {
                try {
                    // Extract just the filename from the URL
                    String fileUrl = content.getFileUrl();
                    String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

                    // Build the full path to your uploads directory
                    Path filePath = Paths.get("C:/uploads/content/" + filename);
                    // OR use relative path: Paths.get("uploads/content/" + filename);

                    Files.deleteIfExists(filePath);
                    System.out.println("Deleted file: " + filePath.toString());
                } catch (IOException e) {
                    System.err.println("Failed to delete file: " );
                }
            }
        }
        contentRepository.deleteAllByIds(ids);
    }

    @Transactional
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));

        if (content.getType() != ContentType.FOLDER && content.getFileUrl() != null) {
            try {
                String fileUrl = content.getFileUrl();
                String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

                Path filePath = Paths.get("C:/uploads/content/" + filename);
                // OR: Paths.get("uploads/content/" + filename);

                boolean deleted = Files.deleteIfExists(filePath);
                if (deleted) {
                    System.out.println("Deleted file: " + filePath.toString());
                } else {
                    System.out.println("File not found: " + filePath.toString());
                }
            } catch (IOException e) {
                System.err.println("Failed to delete file: " );
            }
        }

        contentRepository.delete(content);
    }
}