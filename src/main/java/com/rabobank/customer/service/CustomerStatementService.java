package com.rabobank.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.utils.TxnRecordValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerStatementService {

    public List<TxnRecord> processTransactionRecords(MultipartFile file){
        String fileType = file.getContentType();
        TxnRecordValidationUtil.validateInputFile(file, fileType);
        return validateCustomerTxnRecords(file);
    }

    public List<TxnRecord> validateCustomerTxnRecords(MultipartFile file) {
        TxnRecord[] detail = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            detail = objectMapper.readValue(file.getInputStream(), TxnRecord[].class);
            //LOGGER.info("details " + detail.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<TxnRecord> transactionRecords = Stream.of(detail).collect( Collectors.toList());
        return executeBusinessRules(transactionRecords);
    }

    private List<TxnRecord> executeBusinessRules(List<TxnRecord> transactionRecords){
        Set<String> allItems = new HashSet<>();
        Set<String> refrenceNumbers = transactionRecords.stream().map( txn -> txn.getReference() )
                .filter( txnData -> !allItems.add( txnData ) ).collect( Collectors.toSet() );
        List<TxnRecord> errorRecords = transactionRecords.stream()
                                       .filter( t -> refrenceNumbers.contains( t.getReference() ) )
                                       .peek( t -> t.getFailureReason().add( "DUPLICATE_REFERENCE" ) )
                                       .collect( Collectors.toList() );
        errorRecords.stream().forEach( txn -> {
            if (validateEndBalance( txn )) {
                txn.getFailureReason().add( "BALANCE_MISMATCHED" );
            }
        } );

        List<TxnRecord> incorrectBalanceRecords = transactionRecords.stream()
                .filter( txn -> !refrenceNumbers.contains( txn.getReference()) )
                .filter(txn -> validateEndBalance(txn))
                .peek( txn -> txn.getFailureReason().add("BALANCE_MISMATCHED"))
                .collect( Collectors.toList());
        errorRecords.addAll( incorrectBalanceRecords );
        return errorRecords;
    }

    private boolean validateEndBalance(TxnRecord txnRecord){
        boolean res = false;
        BigDecimal startBalance = new BigDecimal(txnRecord.getStartBalance()).setScale(2);
        BigDecimal mutation = new BigDecimal(txnRecord.getMutation()).setScale(2);
        BigDecimal endBalance = new BigDecimal(txnRecord.getEndBalance()).setScale(2);
        if((startBalance.add(mutation)).compareTo(endBalance)!=0){
            res = true;
        }
        return res;
    }


}
