package com.ajithsolomon.librarymanagement.repository;

import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BorrowedListRepository extends JpaRepository<BorrowedList, Long> {

    Set<BorrowedList> findByUsersId(Long userId);
}
