package com.ajithsolomon.librarymanagement.controller.service;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import com.ajithsolomon.librarymanagement.entity.Users;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.repository.BookRepository;
import com.ajithsolomon.librarymanagement.repository.BorrowedListRepository;
import com.ajithsolomon.librarymanagement.repository.UsersRepository;
import com.ajithsolomon.librarymanagement.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

public class BookServiceTest {

    private static BookService bookService;
    private static UsersRepository usersRepository;
    private static BookRepository bookRepository;
    private static BorrowedListRepository borrowedListRepository;

    @BeforeAll
    public static void setUp() {
        usersRepository = Mockito.mock(UsersRepository.class);
        bookRepository = Mockito.mock(BookRepository.class);
        borrowedListRepository = Mockito.mock(BorrowedListRepository.class);
        bookService = new BookService(bookRepository, usersRepository, borrowedListRepository);
    }

    @Test
    public void findAllBooks(){

        Books book1 = new Books();
        book1.setId(1L);
        book1.setAvailable(true);
        book1.setName("The Keeper of Stories");

        Books book2 = new Books();
        book2.setId(2L);
        book2.setAvailable(false);
        book2.setName("Keepers");

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        bookService.findAllBooks();
    }

    @Test
    public void borrowFirstBookFromLibrary() throws Exception {

        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(2L);
        borrowBookRequest.setUserId(1L);

        Users user = new Users();
        user.setId(1L);
        user.setUsername("user01");
        user.setPassword("password01");

        Books book = new Books();
        book.setId(2L);
        book.setAvailable(true);
        book.setName("The Keeper of Stories");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowedListRepository.findByUsersId(1L)).thenReturn(new HashSet<>());

        bookService.borrowBook(borrowBookRequest);
    }

    @Test
    public void borrowSecondBook() throws Exception {

        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(2L);
        borrowBookRequest.setUserId(1L);

        Users user = new Users();
        user.setId(1L);
        user.setUsername("user01");
        user.setPassword("password01");

        Books book = new Books();
        book.setId(2L);
        book.setAvailable(true);
        book.setName("The Keeper of Stories");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        BorrowedList borrowedList = new BorrowedList();
        borrowedList.setBooks(book);
        borrowedList.setUsers(user);
        borrowedList.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(1L)).thenReturn(Set.of(borrowedList));

        bookService.borrowBook(borrowBookRequest);
    }

    @Test
    public void borrowThirdBook() throws ValidationException, EntityNotFoundException {

        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(2L);
        borrowBookRequest.setUserId(1L);

        Users user = new Users();
        user.setId(1L);
        user.setUsername("user01");
        user.setPassword("password01");

        Books book = new Books();
        book.setId(2L);
        book.setAvailable(true);
        book.setName("The Keeper of Stories");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        BorrowedList borrowedList1 = new BorrowedList();
        borrowedList1.setId(1L);
        borrowedList1.setBooks(book);
        borrowedList1.setUsers(user);
        borrowedList1.setIssuedDate(LocalDateTime.now().toString());

        BorrowedList borrowedList2 = new BorrowedList();
        borrowedList2.setId(2L);
        borrowedList2.setBooks(book);
        borrowedList2.setUsers(user);
        borrowedList2.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(1L)).thenReturn(Set.of(borrowedList1, borrowedList2));
        try{
            bookService.borrowBook(borrowBookRequest);
        }
        catch (ValidationException validationException){
            Assertions.assertEquals("User already borrowed two books",validationException.getMessageAsString());
        }
    }
}
