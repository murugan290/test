package com.rabobank.customer.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabobank.customer.model.TransactionRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author - Murugan Rajendran
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class CustomerValidationResult {

    private String message;
    private List<TransactionRecord> errorRecords;

    public CustomerValidationResult(){

    }

    /**
     *
     * @param message
     * @param errorRecords
     */
    public CustomerValidationResult(String message, List<TransactionRecord> errorRecords) {
        this.message = message;
        this.errorRecords = errorRecords;
    }

}
