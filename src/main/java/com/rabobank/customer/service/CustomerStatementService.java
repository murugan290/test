package com.rabobank.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.model.TransactionRecord;
import com.rabobank.customer.utils.TransactionRecordValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author - Murugan Rajendran
 *
 */


@Service
public class CustomerStatementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerStatementService.class);

    /**
     * This method will parse,process and validate the uploaded file content
     * @param file
     * @return List of TransactionRecord
     *
     */
    public List<TransactionRecord> processTransactionRecords(MultipartFile file){
        TransactionRecordValidationUtil.validateInputFile(file);
        return applyBusinessRules(parseInputFile(file));
    }

    private List<TransactionRecord> parseInputFile(MultipartFile file){
        TransactionRecord[] detail = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            detail = objectMapper.readValue(file.getInputStream(), TransactionRecord[].class);
        } catch (IOException e) {
            LOGGER.error("Parsing file with fileName = {} failed. And the reason is : {}", file.getName() , e);
            throw new FileParsingException(HttpStatus.BAD_REQUEST.value() , Constants.BAD_REQUEST );
        }
        return Stream.of(detail).collect( Collectors.toList());
    }

    private List<TransactionRecord> applyBusinessRules(List<TransactionRecord> transactionRecords){
        Set<String> referenceNumbers = findDuplicateReferenceData( transactionRecords );
        List<TransactionRecord> errorRecords = validateForDuplicateReference( transactionRecords, referenceNumbers );
        List<TransactionRecord> incorrectBalanceRecords = validateForEndBalanceMismatch(transactionRecords, referenceNumbers);
        errorRecords.addAll( incorrectBalanceRecords );
        if(!errorRecords.isEmpty()){
           TransactionRecordValidationUtil.processErrorRecords(errorRecords);
        }
        return errorRecords;
    }

    private List<TransactionRecord> validateForEndBalanceMismatch(List<TransactionRecord> transactionRecords, Set<String> referenceNumbers){
        List<TransactionRecord> incorrectBalanceRecords = transactionRecords.stream()
                .filter( txn -> !referenceNumbers.contains( txn.getReference()) )
                .filter(TransactionRecordValidationUtil::validateEndBalance)
                .collect( Collectors.toList());
        incorrectBalanceRecords.stream().forEach( txn -> txn.getFailureReason().add(Constants.BALANCE_MISMATCHED) );
        return incorrectBalanceRecords;
    }


    private List<TransactionRecord> validateForDuplicateReference(List<TransactionRecord> transactionRecords, Set<String> referenceNumbers){
        List<TransactionRecord> invalidRecords = transactionRecords.stream()
                .filter( txn -> referenceNumbers.contains( txn.getReference() ) )
                .collect( Collectors.toList() );

        invalidRecords.stream().forEach( txn -> {
            txn.getFailureReason().add( Constants.DUPLICATE_REFERENCE );
            if (TransactionRecordValidationUtil.validateEndBalance( txn )) {// segrate this logic seprately
                txn.getFailureReason().add( Constants.BALANCE_MISMATCHED );
            }
        } );
        return invalidRecords;
    }

    private Set<String> findDuplicateReferenceData(List<TransactionRecord> transactionRecords){
        Set<String> customerTxnReference = new HashSet<>();
        return transactionRecords.stream().map( TransactionRecord::getReference)
                .filter( txnData -> !customerTxnReference.add( txnData ) ).collect( Collectors.toSet() );
    }
}
