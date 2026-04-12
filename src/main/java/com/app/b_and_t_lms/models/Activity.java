package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Activity {

    public enum ActionType {
        CREATED, DELETED, UPDATED,ACTIVATED,DEACTIVATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String firstname;

    private String lastname;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ActionType actionType;

    public Activity() {
    }

    public Activity(Long id, String description, String firstname, String lastname, LocalDateTime createdAt,
            ActionType actionType) {
        this.id = id;
        this.description = description;
        this.firstname = firstname;
        this.lastname = lastname;
        this.createdAt = createdAt;
        this.actionType = actionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

}
