package com.app.b_and_t_lms.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Discussion {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @ManyToOne
    private User createdBy;

    private Timestamp createdAt;

    @ManyToOne
    private Program program;

    public Discussion() { 
    }

    public Discussion(Long id, String message, User createdBy, Timestamp createdAt, Program program) {
        this.id = id;
        this.message = message;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.program = program;
    }

    

}
