package com.ajithsolomon.librarymanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uc_users", columnNames = {"id", "username"}))
public class Users {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

}
