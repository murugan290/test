package com.rabobank.customer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.utils.TxnRecordValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
        List<TxnRecord> reports = new ArrayList<>();
        List<String> processedRecords = new ArrayList<>();

        transactionRecords.forEach(txnData ->{

                if(validateTransactionReference(processedRecords,txnData)){
                    reports.add(txnData); // records which failed with given business rules validation like duplication or improper end-balance
                }
                if(validateEndBalance(txnData)){
                    reports.add(txnData);
                }
            processedRecords.add(txnData.getReference());
        });
        processedRecords.clear();
        return reports;
    }

    private boolean validateTransactionReference(List<String> processedRecords,TxnRecord txnRecord){
        boolean res = false;
        if(!txnRecord.getReference().isEmpty() && !processedRecords.isEmpty()
                && processedRecords.contains(txnRecord.getReference())){
            txnRecord.getFailureReason().add("DUPLICATE_REFERENCE");
            res = true;
        }
        return res;
    }

    private boolean validateEndBalance(TxnRecord txnRecord){
        boolean res = false;
        BigDecimal startBalance = new BigDecimal(txnRecord.getStartBalance()).setScale(2);
        BigDecimal mutation = new BigDecimal(txnRecord.getMutation()).setScale(2);
        BigDecimal endBalance = new BigDecimal(txnRecord.getEndBalance()).setScale(2);
        if((startBalance.add(mutation)).compareTo(endBalance)!=0){

            txnRecord.getFailureReason().add("BALANCE_MISMATCHED");
            res = true;
        }
        return res;
    }


}
