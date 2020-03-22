package com.rabobank.customer.exception;

import com.rabobank.customer.model.TransactionRecord;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


/**
 * @author - Murugan Rajendran
 *
 */

@Getter
@Setter
public class DuplicateReferenceException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private final List<TransactionRecord> failedRecords;
    private final  int statusCode;

    public DuplicateReferenceException(int statusCode, List<TransactionRecord> failedRecords, String message) {
        super(message);
        this.failedRecords = failedRecords;
        this.statusCode = statusCode;
    }

}
