package com.rabobank.customer.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabobank.customer.model.TxnRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class ValidationOutcome {

    private String message;
    private List<TxnRecord> errorRecords;

    public ValidationOutcome(){

    }

    /**
     *
     * @param message
     * @param txnRecords
     */
    public ValidationOutcome(String message, List<TxnRecord> errorRecords) {
        //super(message);
        this.message = message;
        this.errorRecords = errorRecords;
    }

   /* *//**
     * @return the recordDetails
     *//*
    public List<TxnRecord> getErrorRecords() {
        return errorRecords;
    }

    *//**
     * @param errorRecords the error txn records to set
     *//*
    public void setErrorRecords(List<TxnRecord> errorRecords) {
        this.errorRecords = errorRecords;
    }*/

}
