package com.app.b_and_t_lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Role {

    public enum RoleName {
        LEARNER,
        FACILITATOR,
        PROGRAM_MANAGER,
        ASSESSOR,
        MODERATOR,
        INTERN,
        MENTOR,
        ADMIN
    }
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName name = RoleName.LEARNER;

    @ManyToOne
    @JsonIgnore
    private User user; 

    public Role() {
    }

    public Role(RoleName name, User user) {
        this.name = name;
        this.user = user;
    }

    public Role(long id, RoleName name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    

}
