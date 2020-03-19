package com.rabobank.customer.utils;


import com.rabobank.customer.exception.DuplicateRefAndBalanceMismatchException;
import com.rabobank.customer.exception.InvalidFileException;
import com.rabobank.customer.exception.UnsupportedFileFormatException;

import com.rabobank.customer.model.TxnRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public class TxnRecordValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxnRecordValidationUtil.class);

    public static void validateInputFile(MultipartFile file, String fileType) {
        // Check for empty file
        if(file.isEmpty()){
            throw new InvalidFileException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Empty File not allowed!!!");
        }
        if(!fileType.equalsIgnoreCase( "application/json" )){
            throw new UnsupportedFileFormatException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    String.join(" ", "The given file format",fileType,"is not supported"));
        }
    }

    public static void processErrorRecords(List<TxnRecord> errorRecords){
        int failedRecords = errorRecords.size();

        long DUPLICATE_REFERENCE_INCORRECT_BALANCE = errorRecords.stream()
                .filter( txn -> (txn.getFailureReason().size() > 1) )
                .count();
        long duplicateTransactionReference = errorRecords.stream().filter(txn -> txn.getFailureReason().get(0).startsWith("DUPLICATE")).count();
        long balanceMismatchRecords = errorRecords.stream().filter(txn -> txn.getFailureReason().get(0).startsWith("BALANCE")).count();
        LOGGER.info( "duplicateTransactionReference " + duplicateTransactionReference );

        if (DUPLICATE_REFERENCE_INCORRECT_BALANCE > 0) {
            throw new DuplicateRefAndBalanceMismatchException( HttpStatus.OK.value(), errorRecords, "DUPLICATE_REFERENCE_INCORRECT_END_BALANCE" );
        }
    }

}
