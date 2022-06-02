package com.ajithsolomon.librarymanagement.controller;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<List<Books>> findAllBooks() {
        List<Books> allBooks = bookService.findAllBooks();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @PatchMapping("/borrow-book")
    public ResponseEntity<Void> borrowBook(@RequestBody BorrowBookRequest borrowBookRequest)
            throws ValidationException, EntityNotFoundException {
        bookService.borrowBook(borrowBookRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}