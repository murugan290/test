package com.rabobank.customer.controller;


import com.rabobank.customer.constants.CustomHttpStatusCode;
import com.rabobank.customer.exception.IncorrectCustomerDataException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.response.ValidationOutcome;
import com.rabobank.customer.service.CustomerStatementService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/statement")
public class CustomerStatementController {

    @Autowired
    CustomerStatementService customerStatementService;

    @PostMapping(value = "/processCustomerStatement")
    @ApiOperation(value = "Make a POST request to upload the file - Only json formats are allowed",produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ValidationOutcome> processInputFile(
           // @ApiParam(name = "file", value = "Select the file to Upload", required = true)
            @RequestParam("file") MultipartFile file) throws IOException {
        //String fileName = file.getOriginalFilename();
        List<TxnRecord> txnRecords = customerStatementService.processTransactionRecords(file);
        return generateResult( txnRecords);
    }



    private ResponseEntity<ValidationOutcome> generateResult(List<TxnRecord> txnRecord) {
        generateFailure( txnRecord);
        //return generateSuccess(result,TxnRecord);
        return generateSuccess("SUCCESSFUL", txnRecord);
    }

    private ResponseEntity<ValidationOutcome> generateSuccess(String result, List<TxnRecord> TxnRecord) {
        ValidationOutcome outcome = new ValidationOutcome(result,new ArrayList<>( ));
        return ResponseEntity.status( HttpStatus.OK).body(outcome);
    }

    private void generateFailure(List<TxnRecord> txnRecord) {
        if (!txnRecord.isEmpty()) {
            throw new IncorrectCustomerDataException( CustomHttpStatusCode.HTTP_STATUS_230, txnRecord, "DUPLICATE_REFERENCE" );
        }
    }

}
