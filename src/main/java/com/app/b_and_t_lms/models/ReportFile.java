package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ReportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileUrl;

    private String fileType; 
    // pdf, docx, xlsx, png, etc.

    @ManyToOne
    private Report report;
}