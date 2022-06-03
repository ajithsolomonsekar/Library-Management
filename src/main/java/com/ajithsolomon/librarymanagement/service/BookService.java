package com.ajithsolomon.librarymanagement.service;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import com.ajithsolomon.librarymanagement.entity.Users;
import com.ajithsolomon.librarymanagement.exception.EntityNotFoundException;
import com.ajithsolomon.librarymanagement.exception.ValidationException;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.repository.BookRepository;
import com.ajithsolomon.librarymanagement.repository.BorrowedListRepository;
import com.ajithsolomon.librarymanagement.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.action.internal.EntityActionVetoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        return bookRepository.findAll().stream().filter(book -> book.getAvailable()).collect(Collectors.toList());
    }

    @Transactional
    public void borrowBook(BorrowBookRequest borrowBookRequest) throws EntityNotFoundException, ValidationException {
        Optional<Users> optionalUser = usersRepository.findById(borrowBookRequest.getUserId());
        if (optionalUser.isPresent()) {
            Optional<Books> optionalBook = bookRepository.findById(borrowBookRequest.getBookId());
            if (optionalBook.isPresent() && optionalBook.get().getAvailable()) {
                Set<BorrowedList> borrowList = borrowedListRepository
                        .findByUsersId(borrowBookRequest.getUserId());
                if (borrowList.size() < 2) {
                    if (isBookAlreadyBorrowed(optionalBook.get(), borrowList)) {
                        throw new ValidationException("Only one copy of a book can be borrowed by the User");
                    }
                    BorrowedList borrowedList = new BorrowedList();
                    Books book = optionalBook.get();
                    book.setAvailable(false);
                    borrowedList.setBooks(book);
                    borrowedList.setUsers(optionalUser.get());
                    borrowedList.setIssuedDate(LocalDateTime.now().toString());
                    borrowedListRepository.save(borrowedList);
                    return;
                }
                throw new ValidationException("User already borrowed two books");
            }
            throw new EntityNotFoundException("Selected book is not available");
        }
        throw new EntityNotFoundException("User not registered in the library system");
    }

    private boolean isBookAlreadyBorrowed(Books book, Set<BorrowedList> borrowList) {
        return borrowList.size() == 1 &&  book.getIsbn().equals(borrowList.stream().findFirst()
                .get().getBooks().getIsbn());
    }


}
