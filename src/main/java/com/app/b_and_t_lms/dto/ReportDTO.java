package com.app.b_and_t_lms.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.app.b_and_t_lms.models.Report;

import lombok.Data;

@Data
public class ReportDTO {

    private Long id;

    private String title;

    private String description;

    private String reportMonth;

    private Long submittedById;

    private LocalDateTime submittedAt;

    private String status;

    private List<ReportFileDTO> files;

    public ReportDTO() {
    }

    public ReportDTO(Report report) {
        this.id = report.getId();
        this.title = report.getTitle();
        this.description = report.getDescription();
        this.reportMonth = report.getReportMonth();
        this.status = report.getStatus().name();
        this.submittedAt = report.getSubmittedAt();

        if (report.getFiles() != null) {
            this.files = report.getFiles()
                    .stream()
                    .map(file -> {
                        ReportFileDTO dto = new ReportFileDTO();
                        dto.setId(file.getId());
                        dto.setFileName(file.getFileName());
                        dto.setFileUrl(file.getFileUrl());
                        dto.setFileType(file.getFileType());
                        return dto;
                    })
                    .toList();
        }
    }

}