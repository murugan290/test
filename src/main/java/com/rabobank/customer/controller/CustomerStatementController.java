package com.rabobank.customer.controller;

import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.response.ValidationOutcome;
import com.rabobank.customer.service.CustomerStatementService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * @author - Murugan Rajendran
 *
 */

@RestController
@RequestMapping("/statement")
public class CustomerStatementController {

    @Autowired
    CustomerStatementService customerStatementService;

    /**
     * This method will process the uploaded file
     * @param file
     * @return ResponseEntity of ValidationOutcome which is empty as all validation checks are passed during file processing
     *
     */
    @PostMapping(value = "/processCustomerStatement")
    @ApiOperation(value = "Make a POST request to upload the file - Only json formats are allowed",produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "The file is valid", response = ValidationOutcome.class),
            @ApiResponse(code = 200, message = "The file has invalid record(s)", response = ValidationOutcome.class),
            @ApiResponse(code = 500, message = "The uploaded file format is not supported", response = ValidationOutcome.class),
            @ApiResponse(code = 500, message = "The uploaded file is empty", response = ValidationOutcome.class),
            @ApiResponse(code = 400, message = "The uploaded file could not be parsed", response = ValidationOutcome.class),
    })
    public ResponseEntity<ValidationOutcome> processInputFile(
            @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestParam("file") MultipartFile file) {
        List<TxnRecord> txnRecords = customerStatementService.processTransactionRecords(file);
        return generateResult( txnRecords);
    }

    private ResponseEntity<ValidationOutcome> generateResult(List<TxnRecord> txnRecord) {
        ValidationOutcome outcome = new ValidationOutcome( Constants.SUCCESS_RESULT,txnRecord);
        return ResponseEntity.status( HttpStatus.OK).body(outcome);
    }

}
