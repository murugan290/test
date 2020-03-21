package com.rabobank.customer.Data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.service.CustomerStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author - Murugan Rajendran
 *
 */

public class CustomerTxnData {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerTxnData.class);

    public List<TxnRecord> getDuplicationReferenceDataSet() throws Exception{
        MockMultipartFile multipartFile = null;
        InputStream is=null;
        File csvFile = new File(this.getClass().getResource("/records_duplicate_reference.json").getFile());
        is = new FileInputStream(csvFile);
        multipartFile = new MockMultipartFile("json", "records_duplicate_reference.json", "application/json", is);
        is.close();
        return getTxnRecords(multipartFile);
    }

    public List<TxnRecord> getIncorrectBalanceDataSet() throws Exception{
        File csvFile = new File(this.getClass().getResource("/records_balance_mismatch.json").getFile());
        InputStream is = new FileInputStream(csvFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "records_balance_mismatch.json", "application/json", is);
        is.close();
        return getTxnRecords(multipartFile);
    }

    public List<TxnRecord> getAllValidDataSet() throws Exception{
        File csvFile = new File(this.getClass().getResource("/records_success.json").getFile());
        InputStream is = new FileInputStream(csvFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "records_success.json", "application/json", is);
        is.close();
        return getTxnRecords(multipartFile);
    }

    public List<TxnRecord> getDuplicateRefAndIncorrentBalDataset() throws Exception{
        File csvFile = new File(this.getClass().getResource("/duplicate_refrence_and_Balance_mismatch_in_same_record.json").getFile());
        InputStream is = new FileInputStream(csvFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "duplicate_refrence_and_Balance_mismatch_in_same_record.json", "application/json", is);
        is.close();
        return getTxnRecords(multipartFile);
    }


    private static List<TxnRecord> getTxnRecords(MultipartFile file){
        ObjectMapper objectMapper = new ObjectMapper();
        TxnRecord[] detail = null;
        try {
            detail = objectMapper.readValue(file.getInputStream(), TxnRecord[].class);
        } catch (IOException e) {
            LOGGER.error("CustomerTxnData :: Parsing file with fileName = {} failed. And the reason is : {}", file.getName() , e);

        }
        List<TxnRecord> transactionRecords = Stream.of(detail).collect( Collectors.toList());
        return transactionRecords;
    }


}
