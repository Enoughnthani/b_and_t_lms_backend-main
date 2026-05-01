package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.dto.ContentRequestDTO;
import com.app.b_and_t_lms.dto.ContentResponseDTO;
import com.app.b_and_t_lms.models.Content;
import com.app.b_and_t_lms.models.Content.ContentType;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.repositories.ContentRepository;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ProgramRepository programRepository;
    private final UnitStandardRepository unitStandardRepository;
    
    private final String UPLOAD_DIR = "uploads/content/";

    // Get root content for a program (no unit standard - general resources)
    public List<ContentResponseDTO> getRootContent(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
            .orElseThrow(() -> new RuntimeException("Program not found"));
        
        return contentRepository.findRootContentByUnitStandardId(unitStandardId)
            .stream()
            .map(ContentResponseDTO::new)
            .collect(Collectors.toList());
    }

    // Get content for a specific unit standard
    public List<ContentResponseDTO> getUnitStandardContent(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
            .orElseThrow(() -> new RuntimeException("Unit Standard not found"));
        
        return contentRepository.findByUnitStandardAndParentIsNull(unitStandard)
            .stream()
            .map(ContentResponseDTO::new)
            .collect(Collectors.toList());
    }

    // Get root content for a specific unit standard (no parent folder)
    public List<ContentResponseDTO> getUnitStandardRootContent(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
            .orElseThrow(() -> new RuntimeException("Unit Standard not found"));
        
        return contentRepository.findByUnitStandardAndParentIsNull(unitStandard)
            .stream()
            .map(ContentResponseDTO::new)
            .collect(Collectors.toList());
    }

    // Get children of a folder (regardless of unit standard)
    public List<ContentResponseDTO> getChildren(Long parentId) {
        return contentRepository.findByParentId(parentId)
            .stream()
            .map(ContentResponseDTO::new)
            .collect(Collectors.toList());
    }

    

    // Create content (folder or link) for a program or unit standard
    @Transactional
    public ContentResponseDTO createContent(ContentRequestDTO dto) {
        Content content = new Content();
        content.setName(dto.getName());
        content.setType(dto.getType());
        
        // Set program (required)
        if (dto.getProgramId() != null) {
            Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found"));
           // content.setProgram(program);
        }
        
        // Set unit standard (optional)
        if (dto.getUnitStandardId() != null) {
            UnitStandard unitStandard = unitStandardRepository.findById(dto.getUnitStandardId())
                .orElseThrow(() -> new RuntimeException("Unit Standard not found"));
            content.setUnitStandard(unitStandard);
        }
        
        // Set parent folder (optional)
        if (dto.getParentId() != null) {
            Content parent = contentRepository.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            content.setParent(parent);
        }

        // Set link URL if type is LINK
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

    // Upload file for program or unit standard
    @Transactional
    public ContentResponseDTO uploadFile(MultipartFile file, String name, ContentType type, 
                                          Long programId, Long unitStandardId, Long parentId) throws IOException {
        
        // Create upload directory if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);
        
        // Save file
        Files.copy(file.getInputStream(), filePath);
        
        // Get file size
        String fileSize = formatFileSize(file.getSize());

        // Create content entity
        Content content = new Content();
        content.setName(name);
        content.setType(type);
        content.setFileUrl("/uploads/content/" + filename);
        content.setFileSize(fileSize);
        content.setDownloadable(true);
        
        if (programId != null) {
            Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));
            //content.setProgram(program);
        }
        
        // Set unit standard (optional)
        if (unitStandardId != null) {
            UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found"));
            content.setUnitStandard(unitStandard);
        }

        // Set parent folder (optional)
        if (parentId != null) {
            Content parent = contentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));
            content.setParent(parent);
        }

        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    // Update content
    @Transactional
    public ContentResponseDTO updateContent(Long id, ContentRequestDTO dto) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found"));

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

    // Delete content
    @Transactional
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Content not found"));
        
        // Delete file from disk if it's a file
        if (content.getType() != ContentType.FOLDER && content.getFileUrl() != null) {
            try {
                Path filePath = Paths.get(content.getFileUrl().replace("/uploads/", "uploads/"));
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
            }
        }
        
        contentRepository.delete(content);
    }

    // Move content to a different unit standard
    @Transactional
    public ContentResponseDTO moveToUnitStandard(Long contentId, Long unitStandardId) {
        Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
        
        if (unitStandardId != null) {
            UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found"));
            content.setUnitStandard(unitStandard);
        } else {
            content.setUnitStandard(null); // Move to root level
        }
        
        Content saved = contentRepository.save(content);
        return new ContentResponseDTO(saved);
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
}