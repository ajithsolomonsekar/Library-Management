package com.ajithsolomon.librarymanagement.model;

import lombok.*;

@Data
public class BorrowBookRequest {

    private Long userId;
    private Long bookId;
}
