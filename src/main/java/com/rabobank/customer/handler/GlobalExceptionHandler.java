package com.rabobank.customer.handler;

import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.exception.IncorrectCustomerDataException;
import com.rabobank.customer.exception.InvalidFileException;
import com.rabobank.customer.exception.UnsupportedFileFormatException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.response.ValidationOutcome;
import com.sun.org.apache.bcel.internal.generic.DUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IncorrectCustomerDataException.class)
    public final ResponseEntity<ValidationOutcome> handleInvalidDataException(IncorrectCustomerDataException ex, WebRequest request){
        log.error("The given file validation is failed as it has invalid data : ", ex);

        int failedRecords = ex.getFailedRecords().size();

        long DUPLICATE_REFERENCE_INCORRECT_BALANCE = ex.getFailedRecords().stream()
                .filter( txn -> (txn.getFailureReason().size() > 1) )
                .count();
        long duplicateTransactionReference = ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("DUPLICATE")).count();
        long balanceMismatchRecords = ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("BALANCE")).count();
        log.info( "duplicateTransactionReference " + duplicateTransactionReference );

        if (DUPLICATE_REFERENCE_INCORRECT_BALANCE > 0) {
            ValidationOutcome result = new ValidationOutcome(
                    "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);

        } else if (duplicateTransactionReference > 0 && duplicateTransactionReference == failedRecords) {
            ValidationOutcome result = new ValidationOutcome(
                    "DUPLICATE_REFERENCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);

        } else if (balanceMismatchRecords > 0 && balanceMismatchRecords == failedRecords) {
            ValidationOutcome result = new ValidationOutcome(
                    "INCORRECT_BALANCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);
        } else {
            ValidationOutcome result = new ValidationOutcome(
                    "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);
        }
    }


    @ExceptionHandler(FileParsingException.class)
    public final ResponseEntity<ValidationOutcome> handleFileParsingException(FileParsingException ex, WebRequest request){
        log.error("The given file validation is failed as the given file is not parsable : ", ex);
        ValidationOutcome result = new ValidationOutcome(
                "BAD_REQUEST",new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(InvalidFileException.class)
    public final ResponseEntity<ValidationOutcome> handleEmptyFileException(InvalidFileException ex, WebRequest request){
        log.error("The given file validation is failed as the file is empty : ", ex);
        ValidationOutcome result = new ValidationOutcome(
                "INTERNAL_SERVER_ERROR",new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }

    @ExceptionHandler(UnsupportedFileFormatException.class)
    public final ResponseEntity<ValidationOutcome> handleUnsupportedFileException(UnsupportedFileFormatException ex, WebRequest request){
        log.error("The given file validation is failed as the format or type is not supported : ", ex);
        ValidationOutcome result = new ValidationOutcome(
                "INTERNAL_SERVER_ERROR",new ArrayList<>( ));
        return ResponseEntity.status(ex.getStatusCode()).body(result);
    }
}
