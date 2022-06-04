package com.ajithsolomon.librarymanagement.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(
        name = "borrowedlist",
        uniqueConstraints = @UniqueConstraint(name = "uc_borrowedlist", columnNames = {"id"}))
public class BorrowedList {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    @JoinColumn(name = "usersId", referencedColumnName = "id")
    private Users users;

    @OneToOne
    @JoinColumn(name = "booksId", referencedColumnName = "id", unique = true)
    private Books books;

    @Column
    private String issuedDate;
}
