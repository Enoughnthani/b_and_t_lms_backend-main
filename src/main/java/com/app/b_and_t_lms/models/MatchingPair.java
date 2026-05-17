package com.app.b_and_t_lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "matching_pairs")
public class MatchingPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String leftItem;

    private String rightItem;

    private Integer displayOrder;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private AssessmentQuestion question;
}