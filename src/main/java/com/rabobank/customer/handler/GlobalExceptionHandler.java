package com.rabobank.customer.handler;

import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.*;
import com.rabobank.customer.response.ValidationOutcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;


/**
 * @author - Murugan Rajendran
 *
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateRefAndBalanceMismatchException.class)
    public final ResponseEntity<ValidationOutcome> handleInvalidDataException(DuplicateRefAndBalanceMismatchException ex, WebRequest request){
        log.error("The given file has duplicate reference & end balance mismatch : ", ex);
        ValidationOutcome result = new ValidationOutcome( ex.getMessage(), ex.getFailedRecords());
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(DuplicateReferenceException.class)
    public final ResponseEntity<ValidationOutcome> handleDuplicateReferenceException(DuplicateReferenceException ex, WebRequest request){
        log.error("The given file has duplicate reference : ", ex);
        ValidationOutcome result = new ValidationOutcome( ex.getMessage(), ex.getFailedRecords());
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(IncorrectEndBalanceException.class)
    public final ResponseEntity<ValidationOutcome> handleIncorrectEndBalanceException(IncorrectEndBalanceException ex, WebRequest request){
        log.error("The given file has incorrect end balance : ", ex);
        ValidationOutcome result = new ValidationOutcome( ex.getMessage(), ex.getFailedRecords());
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(FileParsingException.class)
    public final ResponseEntity<ValidationOutcome> handleFileParsingException(FileParsingException ex, WebRequest request){
        log.error("The given file validation is failed as the given file is not parsable : ", ex);
        ValidationOutcome result = new ValidationOutcome(Constants.BAD_REQUEST,new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(InvalidFileException.class)
    public final ResponseEntity<ValidationOutcome> handleEmptyFileException(InvalidFileException ex, WebRequest request){
        log.error("The given file validation is failed as the file is empty : ", ex);
        ValidationOutcome result = new ValidationOutcome(Constants.INTERNAL_SERVER_ERROR,new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(UnsupportedFileFormatException.class)
    public final ResponseEntity<ValidationOutcome> handleUnsupportedFileException(UnsupportedFileFormatException ex, WebRequest request){
        log.error("The given file validation is failed as the format or type is not supported : ", ex);
        ValidationOutcome result = new ValidationOutcome( Constants.INTERNAL_SERVER_ERROR,new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }
}
