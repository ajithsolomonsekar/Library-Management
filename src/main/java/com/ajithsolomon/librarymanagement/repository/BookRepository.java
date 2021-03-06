package com.ajithsolomon.librarymanagement.repository;

import com.ajithsolomon.librarymanagement.entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Books, Long> {
}
