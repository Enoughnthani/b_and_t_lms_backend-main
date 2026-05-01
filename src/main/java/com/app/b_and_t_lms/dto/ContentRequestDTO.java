package com.app.b_and_t_lms.dto;

import com.app.b_and_t_lms.models.Content.ContentType;
import lombok.Data;

@Data
public class ContentRequestDTO {
    private String name;
    private ContentType type;
    private String fileUrl;
    private String fileSize;
    private String duration;
    private String externalUrl;
    private Boolean downloadable;
    private Long programId;
    private Long unitStandardId;  
    private Long parentId;       
}