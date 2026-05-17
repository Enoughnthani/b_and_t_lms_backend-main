package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "question_options")
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private AssessmentQuestion question;
}