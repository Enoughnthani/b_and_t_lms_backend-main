package com.app.b_and_t_lms.dto;

import com.app.b_and_t_lms.models.Content;
import com.app.b_and_t_lms.models.Content.ContentType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ContentResponseDTO {
    private Long id;
    private String name;
    private ContentType type;
    private String fileUrl;
    private String fileSize;
    private String duration;
    private String externalUrl;
    private Boolean downloadable;
    private Long parentId;
    private String parentName;
    private Integer childrenCount;
    private Long programId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ContentResponseDTO(Content content) {
        this.id = content.getId();
        this.name = content.getName();
        this.type = content.getType();
        this.fileUrl = content.getFileUrl();
        this.fileSize = content.getFileSize();
        this.duration = content.getDuration();
        this.externalUrl = content.getExternalUrl();
        this.downloadable = content.getDownloadable();
        this.parentId = content.getParent() != null ? content.getParent().getId() : null;
        this.parentName = content.getParent() != null ? content.getParent().getName() : null;
        this.childrenCount = content.getChildren() != null ? content.getChildren().size() : 0;
        //this.programId = content.getProgram() != null ? content.getProgram().getId() : null;
        this.createdAt = content.getCreatedAt();
        this.updatedAt = content.getUpdatedAt();
    }
}