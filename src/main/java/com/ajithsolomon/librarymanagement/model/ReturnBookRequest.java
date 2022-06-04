package com.ajithsolomon.librarymanagement.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReturnBookRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long[] bookIdArray;
}
