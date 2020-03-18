package com.rabobank.customer.exception;

import com.rabobank.customer.model.TxnRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class IncorrectCustomerDataException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    private final List<TxnRecord> failedRecords;
    private final  int statusCode;

    public IncorrectCustomerDataException(int statusCode, List<TxnRecord> failedRecords, String message) {
        super(message);
        this.failedRecords = failedRecords;
        this.statusCode = statusCode;
    }
}
