package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "program_id")
    @JsonIgnore
    private Program program;

    private LocalDateTime enrollmentDate;

    public Enrollment(Long id, User user, Program program, LocalDateTime enrollmentDate) {
        this.id = id;
        this.user = user;
        this.program = program;
        this.enrollmentDate = enrollmentDate;
    }

    public Enrollment() {
    }

}
