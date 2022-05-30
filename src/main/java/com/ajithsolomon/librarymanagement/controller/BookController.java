package com.ajithsolomon.librarymanagement.controller;

import com.ajithsolomon.librarymanagement.entity.Book;
import com.ajithsolomon.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> findAllBooks(){

        List<Book> allBooks = bookService.findAllBooks();

        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }
}