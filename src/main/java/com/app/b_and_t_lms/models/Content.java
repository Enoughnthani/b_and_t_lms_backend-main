package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Content {

    public enum ContentType {
        FOLDER, PDF, DOCX, XLSX, PPTX, VIDEO, LINK, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ContentType type;

    private String fileUrl;

    private String fileSize;

    private String duration;

    private String externalUrl;

    private Boolean downloadable = true;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Content parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> children = new ArrayList<>();

    @ManyToOne
    private UnitStandard unitStandard;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}