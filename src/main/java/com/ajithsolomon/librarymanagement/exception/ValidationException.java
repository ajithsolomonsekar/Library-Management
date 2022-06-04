package com.ajithsolomon.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Some validations failed ...")
public class ValidationException extends Exception {

    private static final long serialVersionUID = 1987130135699527245L;
    private final List<String> errors = new ArrayList<>();
    public ValidationException(String error) {
        addErrors(error);
    }
    public void addErrors(String error) {
        errors.add(error);
    }
    public String getMessageAsString() {
        return String.join(", ", errors);
    }
}
