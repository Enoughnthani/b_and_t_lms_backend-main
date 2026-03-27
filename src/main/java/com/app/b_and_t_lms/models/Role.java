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

@Entity
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

    public boolean isLearner() {
        return name.equals(RoleName.LEARNER);
    }

    public boolean isAdmin() {
        return name.equals(RoleName.ADMIN);
    }

    public boolean isFacilitator() {
        return name.equals(RoleName.FACILITATOR);

    }

    public boolean isAssessor() {
        return name.equals(RoleName.ASSESSOR);
    }

    public boolean isModerator() {
        return name.equals(RoleName.MODERATOR);
    }

    public boolean isMentor() {
        return name.equals(RoleName.MENTOR);
    }

    public boolean isIntern() {
        return name.equals(RoleName.INTERN);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
