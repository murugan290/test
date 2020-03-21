package com.rabobank.customer.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.rabobank.customer.model.TxnRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author - Murugan Rajendran
 *
 */

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
     * @param errorRecords
     */
    public ValidationOutcome(String message, List<TxnRecord> errorRecords) {
        this.message = message;
        this.errorRecords = errorRecords;
    }

}
