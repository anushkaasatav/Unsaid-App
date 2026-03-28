package com.unsaid.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String handle;

    public User() {}

    public User(String handle) {
        this.handle = handle;
    }

    public Long getId() {
        return id;
    }

    public String getHandle() {
        return handle;
    }
}
