package com.rabobank.customer.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author - Murugan Rajendran
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@JsonPropertyOrder({"reference","accountNumber","failureReason"})
public class TxnRecord implements Serializable{


	private static final long serialVersionUID = 1L;

	@JsonProperty("reference")
	@JsonAlias("Reference")
	private String reference;


	@JsonProperty("accountNumber")
	@JsonAlias("AccountNumber")
	private String accountNumber;


	@JsonProperty(value = "Description", access = JsonProperty.Access.WRITE_ONLY)
	private String description;


	@JsonProperty(value="Start Balance", access = JsonProperty.Access.WRITE_ONLY)
	private String startBalance;


	@JsonProperty(value="Mutation", access = JsonProperty.Access.WRITE_ONLY)
	private String mutation;


	@JsonProperty(value ="End Balance" , access = JsonProperty.Access.WRITE_ONLY)
	private String endBalance;


	private List<String> failureReason = new ArrayList<>();

}
