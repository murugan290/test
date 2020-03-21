package com.rabobank.customer.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author - Murugan Rajendran
 *
 */


@Getter
@Setter
public class FileParsingException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private final int statusCode;

    public FileParsingException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

}
