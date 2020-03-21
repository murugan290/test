package com.rabobank.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.utils.TxnRecordValidationUtil;
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
     * @return List of TxnRecord
     *
     */
    public List<TxnRecord> processTransactionRecords(MultipartFile file){
        String fileType = file.getContentType();
        TxnRecordValidationUtil.validateInputFile(file, fileType);
        return validateCustomerTxnRecords(file);
    }

    private List<TxnRecord> validateCustomerTxnRecords(MultipartFile file){
        TxnRecord[] detail = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            detail = objectMapper.readValue(file.getInputStream(), TxnRecord[].class);
        } catch (IOException e) {
            LOGGER.error("Parsing file with fileName = {} failed. And the reason is : {}", file.getName() , e);
            throw new FileParsingException(HttpStatus.BAD_REQUEST.value() , Constants.BAD_REQUEST );
        }
        List<TxnRecord> transactionRecords = Stream.of(detail).collect( Collectors.toList());
        return executeBusinessRules(transactionRecords);
    }

    private List<TxnRecord> executeBusinessRules(List<TxnRecord> transactionRecords){

        Set<String> referenceNumbers = findDuplicateReferenceData( transactionRecords );
        List<TxnRecord> errorRecords = updateFailureReasonInDuplicateReferenceRecords( transactionRecords, referenceNumbers );
        List<TxnRecord> incorrectBalanceRecords = findEndBalanceMismatchRecords(transactionRecords, referenceNumbers);
        errorRecords.addAll( incorrectBalanceRecords );
        if(!errorRecords.isEmpty()){
            TxnRecordValidationUtil.processErrorRecords(errorRecords);
        }
        return errorRecords;
    }

    private List<TxnRecord> findEndBalanceMismatchRecords(List<TxnRecord> transactionRecords, Set<String> referenceNumbers){
        List<TxnRecord> incorrectBalanceRecords = transactionRecords.stream()
                .filter( txn -> !referenceNumbers.contains( txn.getReference()) )
                .filter(TxnRecordValidationUtil::validateEndBalance)
                .collect( Collectors.toList());
        incorrectBalanceRecords.stream().forEach( txn -> txn.getFailureReason().add(Constants.BALANCE_MISMATCHED) );
        return incorrectBalanceRecords;
    }

    private List<TxnRecord> updateFailureReasonInDuplicateReferenceRecords(List<TxnRecord> transactionRecords, Set<String> referenceNumbers){
        List<TxnRecord> errorRecords = transactionRecords.stream()
                .filter( txn -> referenceNumbers.contains( txn.getReference() ) )
                .collect( Collectors.toList() );

        errorRecords.stream().forEach( txn -> {
            txn.getFailureReason().add( Constants.DUPLICATE_REFERENCE );
            if (TxnRecordValidationUtil.validateEndBalance( txn )) {
                txn.getFailureReason().add( Constants.BALANCE_MISMATCHED );
            }
        } );
        return errorRecords;
    }

    private Set<String> findDuplicateReferenceData(List<TxnRecord> transactionRecords){
        Set<String> customerTxnReference = new HashSet<>();
        return transactionRecords.stream().map( TxnRecord::getReference)
                .filter( txnData -> !customerTxnReference.add( txnData ) ).collect( Collectors.toSet() );
    }
}
