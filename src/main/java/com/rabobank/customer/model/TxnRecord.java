package com.rabobank.customer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Philomin Raj
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonIgnoreProperties(value={ "Description", "Mutation","Start Balance","End Balance" }, allowGetters= true)
@Getter
@Setter
public class TxnRecord implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	//@XmlAttribute(name = "reference")
	@JsonProperty("Reference")
	private String reference;

	//@JsonIgnore
	@JsonProperty("AccountNumber")
	private String accountNumber;

	//@JsonIgnore
	@JsonProperty(value = "Description", access = JsonProperty.Access.WRITE_ONLY)
	private String description;


	@JsonProperty(value="Start Balance", access = JsonProperty.Access.WRITE_ONLY)
	private String startBalance;


	@JsonProperty(value="Mutation", access = JsonProperty.Access.WRITE_ONLY)
	private String mutation;

	@JsonIgnore
	@JsonProperty(value ="End Balance" , access = JsonProperty.Access.WRITE_ONLY)
	private String endBalance;

	//@JsonIgnore
	private List<String> failureReason = new ArrayList<>();

}
