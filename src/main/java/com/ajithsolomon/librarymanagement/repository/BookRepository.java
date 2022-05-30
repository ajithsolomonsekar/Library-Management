package com.ajithsolomon.librarymanagement.repository;

import com.ajithsolomon.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
