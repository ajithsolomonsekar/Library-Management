package com.ajithsolomon.librarymanagement.controller.service;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import com.ajithsolomon.librarymanagement.entity.Users;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.model.ReturnBookRequest;
import com.ajithsolomon.librarymanagement.repository.BookRepository;
import com.ajithsolomon.librarymanagement.repository.BorrowedListRepository;
import com.ajithsolomon.librarymanagement.repository.UsersRepository;
import com.ajithsolomon.librarymanagement.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
    public void findAllBooksTest(){

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
    public void borrowOneBookFromLibraryTest() throws Exception {

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
    public void borrowSecondBookFromLibraryTest() throws Exception {

        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(3L);
        borrowBookRequest.setUserId(1L);

        Users user = new Users();
        user.setId(1L);
        user.setUsername("user01");
        user.setPassword("password01");

        Books book1 = new Books();
        book1.setId(2L);
        book1.setAvailable(false);
        book1.setName("The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = new Books();
        book2.setId(3L);
        book2.setAvailable(true);
        book2.setName("Mayo's Famous Son");
        book2.setIsbn("9781838770013");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book2));

        BorrowedList borrowedList = new BorrowedList();
        borrowedList.setBooks(book1);
        borrowedList.setUsers(user);
        borrowedList.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(1L)).thenReturn(Set.of(borrowedList));

        bookService.borrowBook(borrowBookRequest);
    }

    @Test
    public void borrowThirdBookReturnExceptionTest() throws EntityNotFoundException {

        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(4L);
        borrowBookRequest.setUserId(1L);

        Users user = new Users();
        user.setId(1L);
        user.setUsername("user01");
        user.setPassword("password01");

        Books book1 = new Books();
        book1.setId(2L);
        book1.setAvailable(false);
        book1.setName("The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = new Books();
        book2.setId(3L);
        book2.setAvailable(false);
        book2.setName("Stories");
        book2.setIsbn("9781838770013");

        Books book3 = new Books();
        book3.setId(4L);
        book3.setAvailable(true);
        book3.setName("Songbirds");
        book3.setIsbn("9781566199094");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book3));
        BorrowedList borrowedList1 = new BorrowedList();
        borrowedList1.setId(1L);
        borrowedList1.setBooks(book1);
        borrowedList1.setUsers(user);
        borrowedList1.setIssuedDate(LocalDateTime.now().toString());

        BorrowedList borrowedList2 = new BorrowedList();
        borrowedList2.setId(2L);
        borrowedList2.setBooks(book2);
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

    @Test
    public void borrowSecondCopyOfSameBookReturnExceptionTest() throws EntityNotFoundException{
        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(6L);
        borrowBookRequest.setUserId(2L);

        Users user = new Users();
        user.setId(2L);
        user.setUsername("user02");
        user.setPassword("password02");

        Books book1 = new Books();
        book1.setId(5L);
        book1.setAvailable(false);
        book1.setName("The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = new Books();
        book2.setId(6L);
        book2.setAvailable(true);
        book2.setName("The Keeper of Stories");
        book2.setIsbn("9780008453510");

        when(usersRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(6L)).thenReturn(Optional.of(book2));

        BorrowedList borrowedList = new BorrowedList();
        borrowedList.setId(1L);
        borrowedList.setBooks(book1);
        borrowedList.setUsers(user);
        borrowedList.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(2L)).thenReturn(Set.of(borrowedList));
        try{
            bookService.borrowBook(borrowBookRequest);
        }
        catch (ValidationException validationException){
            Assertions.assertEquals("Only one copy of a book can be borrowed by the User",validationException.getMessageAsString());
        }
    }

    @Test
    public void returnOneBookToLibraryTest() {
        ReturnBookRequest returnBookRequest = new ReturnBookRequest();
        returnBookRequest.setUserId(2L);
        returnBookRequest.setBookIdArray(new Long[]{4L});

        Users user = new Users();
        user.setId(2L);
        user.setUsername("user02");
        user.setPassword("password02");

        Books book = new Books();
        book.setId(4L);
        book.setAvailable(false);
        book.setName("The Keeper of Stories");
        book.setIsbn("9780008453510");

        BorrowedList borrowedList = new BorrowedList();
        borrowedList.setId(1L);
        borrowedList.setBooks(book);
        borrowedList.setUsers(user);
        borrowedList.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(2L)).thenReturn(Set.of(borrowedList));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book));
        bookService.returnBook(returnBookRequest);
    }

    @Test
    public void returnTwoBooksToLibraryTest() {
        ReturnBookRequest returnBookRequest = new ReturnBookRequest();
        returnBookRequest.setUserId(2L);
        returnBookRequest.setBookIdArray(new Long[]{4L});

        Users user = new Users();
        user.setId(2L);
        user.setUsername("user02");
        user.setPassword("password02");

        Books book1 = new Books();
        book1.setId(4L);
        book1.setAvailable(false);
        book1.setName("The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = new Books();
        book2.setId(5L);
        book2.setAvailable(false);
        book2.setName("Songbirds");
        book2.setIsbn("9781566199094");

        BorrowedList borrowedList1 = new BorrowedList();
        borrowedList1.setId(1L);
        borrowedList1.setBooks(book1);
        borrowedList1.setUsers(user);
        borrowedList1.setIssuedDate(LocalDateTime.now().toString());

        BorrowedList borrowedList2 = new BorrowedList();
        borrowedList2.setId(2L);
        borrowedList2.setBooks(book2);
        borrowedList2.setUsers(user);
        borrowedList2.setIssuedDate(LocalDateTime.now().toString());

        when(borrowedListRepository.findByUsersId(2L)).thenReturn(Set.of(borrowedList1, borrowedList2));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book2));
        bookService.returnBook(returnBookRequest);
    }

}
