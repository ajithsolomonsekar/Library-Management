package com.ajithsolomon.librarymanagement.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class ErrorResponse {

    private ZonedDateTime timestamp = ZonedDateTime.now();
    private Integer status;
    private String error;
    private String message;
}
