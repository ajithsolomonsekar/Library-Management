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
        Books book1 = getBook1(1L, true, "The Keeper of Stories");
        Books book2 = getBook1(2L, false, "Keepers");
        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        bookService.findAllBooks();
    }

    private Books getBook1(long id, boolean available, String The_Keeper_of_Stories) {
        Books book1 = new Books();
        book1.setId(id);
        book1.setAvailable(available);
        book1.setName(The_Keeper_of_Stories);
        return book1;
    }

    @Test
    public void borrowOneBookFromLibraryTest() throws Exception {
        BorrowBookRequest borrowBookRequest = getBorrowBookRequest(2L, 1L);

        Users user = getUser(1L, "user01", "password01");
        Books book = getBook1(2L, true, "The Keeper of Stories");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowedListRepository.findByUsersId(1L)).thenReturn(new HashSet<>());

        bookService.borrowBook(borrowBookRequest);
    }

    private BorrowBookRequest getBorrowBookRequest(long bookId, long userId) {
        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(bookId);
        borrowBookRequest.setUserId(userId);
        return borrowBookRequest;
    }

    private Users getUser(long id, String user01, String password01) {
        Users user = new Users();
        user.setId(id);
        user.setUsername(user01);
        user.setPassword(password01);
        return user;
    }

    @Test
    public void borrowSecondBookFromLibraryTest() throws Exception {
        BorrowBookRequest borrowBookRequest = getBorrowBookRequest(3L, 1L);

        Users user = getUser(1L, "user01", "password01");

        Books book1 = getBook1(2L, false, "The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = getBook1(3L, true, "Mayo's Famous Son");
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

        BorrowBookRequest borrowBookRequest = getBorrowBookRequest(4L, 1L);

        Users user = getUser(1L, "user01", "password01");

        Books book1 = getBook1(2L, false, "The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = getBook1(3L, false, "Stories");
        book2.setIsbn("9781838770013");

        Books book3 = getBook1(4L, true, "Songbirds");
        book3.setIsbn("9781566199094");

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book3));
        BorrowedList borrowedList1 = getBorrowedList(1L, book1, user);

        BorrowedList borrowedList2 = getBorrowedList(2L, book2, user);

        when(borrowedListRepository.findByUsersId(1L)).thenReturn(Set.of(borrowedList1, borrowedList2));
        try{
            bookService.borrowBook(borrowBookRequest);
        }
        catch (ValidationException validationException){
            Assertions.assertEquals("User already borrowed two books",validationException.getMessageAsString());
        }
    }

    private BorrowedList getBorrowedList(long id, Books book1, Users user) {
        BorrowedList borrowedList1 = new BorrowedList();
        borrowedList1.setId(id);
        borrowedList1.setBooks(book1);
        borrowedList1.setUsers(user);
        borrowedList1.setIssuedDate(LocalDateTime.now().toString());
        return borrowedList1;
    }

    @Test
    public void borrowSecondCopyOfSameBookReturnExceptionTest() throws EntityNotFoundException{
        BorrowBookRequest borrowBookRequest = getBorrowBookRequest(6L, 2L);

        Users user = getUser(2L, "user02", "password02");

        Books book1 = getBook1(5L, false, "The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = getBook1(6L, true, "The Keeper of Stories");
        book2.setIsbn("9780008453510");

        when(usersRepository.findById(2L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(6L)).thenReturn(Optional.of(book2));

        BorrowedList borrowedList = getBorrowedList(1L, book1, user);

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
        ReturnBookRequest returnBookRequest = getReturnBookRequest();

        Users user = getUser(2L, "user02", "password02");

        Books book = getBook1(4L, false, "The Keeper of Stories");
        book.setIsbn("9780008453510");

        BorrowedList borrowedList = getBorrowedList(1L, book, user);

        when(borrowedListRepository.findByUsersId(2L)).thenReturn(Set.of(borrowedList));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book));
        bookService.returnBook(returnBookRequest);
    }

    private ReturnBookRequest getReturnBookRequest() {
        ReturnBookRequest returnBookRequest = new ReturnBookRequest();
        returnBookRequest.setUserId(2L);
        returnBookRequest.setBookIdArray(new Long[]{4L});
        return returnBookRequest;
    }

    @Test
    public void returnTwoBooksToLibraryTest() {
        ReturnBookRequest returnBookRequest = getReturnBookRequest();

        Users user = getUser(2L, "user02", "password02");

        Books book1 = getBook1(4L, false, "The Keeper of Stories");
        book1.setIsbn("9780008453510");

        Books book2 = getBook1(5L, false, "Songbirds");
        book2.setIsbn("9781566199094");

        BorrowedList borrowedList1 = getBorrowedList(1L, book1, user);
        BorrowedList borrowedList2 = getBorrowedList(2L, book2, user);

        when(borrowedListRepository.findByUsersId(2L)).thenReturn(Set.of(borrowedList1, borrowedList2));
        when(bookRepository.findById(4L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book2));
        bookService.returnBook(returnBookRequest);
    }

}
