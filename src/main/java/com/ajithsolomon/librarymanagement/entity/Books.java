package com.ajithsolomon.librarymanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(
        name = "books",
        uniqueConstraints = @UniqueConstraint(name = "uc_books", columnNames = {"id"}))
public class Books {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String author;

    @Column
    private String publisher;

    @Column
    private String edition;

    @Column(nullable = false)
    private Boolean available;

}
