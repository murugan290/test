package com.rabobank.customer.handler;

import com.rabobank.customer.exception.IncorrectCustomerDataException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.response.ValidationOutcome;
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

       long DUPLICATE_REFERENCE_INCORRECT_BALANCE = ex.getFailedRecords().stream().filter(txn ->
            (txn.getFailureReason().size()>1 || (txn.getFailureReason().size()==1 && txn.getFailureReason().get(0).startsWith("DUPLICATE") ||
                    txn.getFailureReason().get(0).startsWith("BALANCE") ))
        ).count();

        long DUPLICATE_REFERENCE = ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("DUPLICATE")).count();

        long INCORRECT_BALANCE = ex.getFailedRecords().stream().filter(txn -> txn.getFailureReason().get(0).startsWith("BALANCE")).count();

        if(failedRecords == INCORRECT_BALANCE){
            ValidationOutcome result = new ValidationOutcome(
                    "INCORRECT_END_BALANCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);
        }else if(failedRecords == DUPLICATE_REFERENCE){
            ValidationOutcome result = new ValidationOutcome(
                    "DUPLICATE_REFERENCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);
        }else /*(failedRecords == INCORRECT_BALANCE) */{
            ValidationOutcome result = new ValidationOutcome(
                    "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE", ex.getFailedRecords());
            return ResponseEntity.status(ex.getStatusCode()).body(result);
        }



        /*long count = ex.getFailedRecords().stream().filter(t -> t.getFailureReason().get(0).startsWith("DUPLICATE") ||
                t.getFailureReason().get(0).startsWith("BALANCE") ).count();



        if(count>1){
            log.info("success..........");
        }*/


    }

}
