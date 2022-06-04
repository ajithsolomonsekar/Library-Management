package com.ajithsolomon.librarymanagement.service;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.model.ReturnBookRequest;
import com.ajithsolomon.librarymanagement.repository.BookRepository;
import com.ajithsolomon.librarymanagement.repository.BorrowedListRepository;
import com.ajithsolomon.librarymanagement.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final UsersRepository usersRepository;
    private final BorrowedListRepository borrowedListRepository;

    @Autowired
    public BookService(BookRepository bookRepository, UsersRepository usersRepository, BorrowedListRepository borrowedListRepository) {
        this.bookRepository = bookRepository;
        this.usersRepository = usersRepository;
        this.borrowedListRepository = borrowedListRepository;
    }

    public List<Books> findAllBooks() {
        return bookRepository.findAll().stream().filter(Books::getAvailable).collect(Collectors.toList());
    }

    @Transactional
    public void borrowBook(BorrowBookRequest borrowBookRequest) throws EntityNotFoundException, ValidationException {
        var optionalUser = usersRepository.findById(borrowBookRequest.getUserId());
        if (optionalUser.isPresent()) {
            var optionalBook = bookRepository.findById(borrowBookRequest.getBookId());
            if (optionalBook.isPresent() && optionalBook.get().getAvailable()) {
                var borrowList = borrowedListRepository
                        .findByUsersId(borrowBookRequest.getUserId());
                if (borrowList.size() < 2) {
                    if (isBookAlreadyBorrowed(optionalBook.get(), borrowList)) {
                        throw new ValidationException("Only one copy of a book can be borrowed by the User");
                    }
                    var borrowedList = new BorrowedList();
                    var book = optionalBook.get();
                    book.setAvailable(false);
                    borrowedList.setBooks(book);
                    borrowedList.setUsers(optionalUser.get());
                    borrowedList.setIssuedDate(LocalDateTime.now().toString());
                    borrowedListRepository.save(borrowedList);
                    log.info("User {} borrowed the book: {}", optionalUser.get().getUsername(), book.getName());
                    return;
                }
                throw new ValidationException("User already borrowed two books");
            }
            throw new EntityNotFoundException("Selected book is not available");
        }
        throw new EntityNotFoundException("User not registered in the library system");
    }

    private boolean isBookAlreadyBorrowed(Books book, Set<BorrowedList> borrowList) {
        return borrowList.size() == 1 && book.getIsbn().equals(borrowList.iterator().next().getBooks().getIsbn());
    }

    @Transactional
    public void returnBook(ReturnBookRequest returnBookRequest) {
        var borrowList = borrowedListRepository.findByUsersId(returnBookRequest.getUserId());

        Arrays.stream(returnBookRequest.getBookIdArray()).forEach(bookId -> {
            var borrowedListOptional = borrowList.stream().filter(borrowedList ->
                    bookId.equals(borrowedList.getBooks().getId())).findFirst();
            if (borrowedListOptional.isPresent()) {
                var book = bookRepository.findById(bookId);
                book.ifPresent(books -> {
                    books.setAvailable(true);
                    bookRepository.save(book.get());
                });
                borrowedListRepository.deleteById(borrowedListOptional.get().getId());
                log.info("Book returned to the library: {}", book.get().getName());
            }
            else{
                log.warn("Book Id {} not found in borrowed list", bookId);
            }
        });

    }
}
