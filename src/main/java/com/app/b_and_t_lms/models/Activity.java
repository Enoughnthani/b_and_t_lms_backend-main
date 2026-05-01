package com.app.b_and_t_lms.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Activity {

    public enum ActionType {
        CREATED,
        DELETED,
        UPDATED,
        ROLE_ASSIGN,
        ACTIVATED,
        DEACTIVATED,
        BULK_DELETE,
        BULK_CREATE,
        BULK_ROLE_ASSIGN,
        BULK_STATUS_UPDATE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private String firstname;

    private String lastname;

    private String message;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ActionType actionType;

    public Activity() {
    }


    public Activity(Long id, String description, String firstname, String lastname, String message,
            LocalDateTime createdAt, ActionType actionType) {
        this.id = id;
        this.description = description;
        this.firstname = firstname;
        this.lastname = lastname;
        this.message = message;
        this.createdAt = createdAt;
        this.actionType = actionType;
    }    

}
