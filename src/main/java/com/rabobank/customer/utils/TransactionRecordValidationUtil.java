package com.rabobank.customer.utils;


import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.*;
import com.rabobank.customer.model.TransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author - Murugan Rajendran
 *
 */


public class TransactionRecordValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRecordValidationUtil.class);

    private TransactionRecordValidationUtil() {

    }

    /**
     * This method will validate the file type and content
     * @param file
     * @throw InvalidFileException
     * @throw UnsupportedFileFormatException
     */
    public static void validateInputFile(MultipartFile file) {
        // Check for empty file
        if(file.isEmpty()){
            throw new InvalidFileException(HttpStatus.INTERNAL_SERVER_ERROR.value(), Constants.FILE_CANT_BE_EMPTY );
        }
        if(!file.getContentType().equalsIgnoreCase( Constants.JSON_CONTENT_TYPE )){
            throw new UnsupportedFileFormatException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    String.join(" ", "The given file format",file.getContentType(),"is not supported"));
        }
    }

    public static void processErrorRecords(List<TransactionRecord> errorRecords){
        int failedRecords = errorRecords.size();

        long duplicateReferenceAndIncorrectBalance = errorRecords.stream().filter( txn -> (txn.getFailureReason().size() > 1) ).count();
        long duplicateTransactionReference = errorRecords.stream().filter( txn -> txn.getFailureReason().get( 0 ).startsWith( Constants.DUPLICATE_CHECK ) ).count();
        long balanceMismatchRecords = errorRecords.stream().filter( txn -> txn.getFailureReason().get( 0 ).startsWith( Constants.BALANCE_CHECK ) ).count();

        LOGGER.info( "duplicateTransactionReference {} " , duplicateTransactionReference );

        if (duplicateReferenceAndIncorrectBalance > 0) {
            throw new DuplicateRefAndBalanceMismatchException( HttpStatus.OK.value(), errorRecords, Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE );
        } else if (duplicateTransactionReference > 0 && duplicateTransactionReference == failedRecords) {
            throw new DuplicateReferenceException( HttpStatus.OK.value(), errorRecords, Constants.DUPLICATE_REFERENCE);
        } else if (balanceMismatchRecords > 0 && balanceMismatchRecords == failedRecords) {
            throw new IncorrectEndBalanceException( HttpStatus.OK.value(), errorRecords, Constants.INCORRECT_END_BALANCE );
        } else {
            throw new DuplicateRefAndBalanceMismatchException( HttpStatus.OK.value(), errorRecords, Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE );
        }
    }

    public static boolean validateEndBalance(TransactionRecord txnRecord){
        boolean response = false;
        BigDecimal startBalance = new BigDecimal(txnRecord.getStartBalance()).setScale(2);
        BigDecimal mutation = new BigDecimal(txnRecord.getMutation()).setScale(2);
        BigDecimal endBalance = new BigDecimal(txnRecord.getEndBalance()).setScale(2);
        if((startBalance.add(mutation)).compareTo(endBalance)!=0){
            response = true;
        }
        return response;
    }

}
