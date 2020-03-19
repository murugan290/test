package com.rabobank.customer.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidFileException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private final int statusCode;

    public InvalidFileException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
