package com.rabobank.customer.handler;

import com.rabobank.customer.exception.IncorrectCustomerDataException;
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
       if(DUPLICATE_REFERENCE_INCORRECT_BALANCE > 0){
           ValidationOutcome result = new ValidationOutcome(
                   "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", ex.getFailedRecords());
           return ResponseEntity.status(ex.getStatusCode()).body(result);

       }else if(ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("DUPLICATE")).count() > 0 &&
               ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("DUPLICATE")).count() == failedRecords)
       {
           ValidationOutcome result = new ValidationOutcome(
                   "DUPLICATE_REFERENCE", ex.getFailedRecords());
           return ResponseEntity.status(ex.getStatusCode()).body(result);

       }else if (ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("BALANCE")).count() > 0 &&
                ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("BALANCE")).count() == failedRecords){
           ValidationOutcome result = new ValidationOutcome(
                   "INCORRECT_BALANCE", ex.getFailedRecords());
           return ResponseEntity.status(ex.getStatusCode()).body(result);
       }else{
           ValidationOutcome result = new ValidationOutcome(
                   "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", ex.getFailedRecords());
           return ResponseEntity.status(ex.getStatusCode()).body(result);
       }
    }

}
