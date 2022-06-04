package com.ajithsolomon.librarymanagement.controller;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.model.ReturnBookRequest;
import com.ajithsolomon.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<List<Books>> findAllBooks() {
        return new ResponseEntity<>(bookService.findAllBooks(), HttpStatus.OK);
    }

    @PatchMapping("/borrow-book")
    public ResponseEntity<Void> borrowBook(@RequestBody @Valid BorrowBookRequest borrowBookRequest)
            throws ValidationException, EntityNotFoundException {
        bookService.borrowBook(borrowBookRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/return-book")
    public ResponseEntity<Void> returnBook(@RequestBody @Valid ReturnBookRequest returnBookRequest)
            throws ValidationException {
        bookService.returnBook(returnBookRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}