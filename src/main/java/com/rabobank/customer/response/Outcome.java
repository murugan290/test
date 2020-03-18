package com.rabobank.customer.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class Outcome {

    private String message;

    public Outcome(){

    }

    public Outcome( String message) {
        super();
        this.message = message;
    }
}
