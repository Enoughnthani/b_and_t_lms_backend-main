package com.app.b_and_t_lms.dto;

import lombok.Data;

@Data
public class ReportFileDTO {

    private Long id;

    private String fileName;

    private String fileUrl;

    private String fileType;

    private Long fileSize;

    public ReportFileDTO() {
    }

    public ReportFileDTO(Long id, String fileName, String fileUrl, String fileType, Long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}