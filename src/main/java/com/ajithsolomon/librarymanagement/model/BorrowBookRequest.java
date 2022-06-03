package com.ajithsolomon.librarymanagement.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class BorrowBookRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long bookId;
}
