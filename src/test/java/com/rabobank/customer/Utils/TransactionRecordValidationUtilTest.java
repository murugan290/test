package com.rabobank.customer.Utils;

import com.rabobank.customer.exception.*;
import com.rabobank.customer.model.TransactionRecord;
import com.rabobank.customer.utils.TransactionRecordValidationUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
public class TransactionRecordValidationUtilTest {


    TransactionRecordValidationUtil transactionRecordValidationUtil;

    @Test(expected = InvalidFileException.class)
    public void validateForEmptyInputFileTest() throws Exception{
        File jsonFile= new File(this.getClass().getResource("/records_empty.json").getFile());
        InputStream is = new FileInputStream(jsonFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", null, null, is);
        TransactionRecordValidationUtil.validateInputFile(multipartFile);
    }

    @Test(expected = UnsupportedFileFormatException.class)
    public void validateForUnsupportedInputFileTest() throws Exception{
        File txt= new File(this.getClass().getResource("/records.txt").getFile());
        InputStream is = new FileInputStream(txt);
        MockMultipartFile multipartFile = new MockMultipartFile("txt", null, "text", is);
        TransactionRecordValidationUtil.validateInputFile(multipartFile);
    }

    @Test
    public void validationEndBalanceSuccessTest(){
        TransactionRecord txn = new TransactionRecord();
        txn.setStartBalance("15.25");
        txn.setMutation("10.25");
        txn.setEndBalance("25.50");
        boolean outcome = transactionRecordValidationUtil.validateEndBalance(txn);
        Assert.assertFalse(outcome);
    }

    @Test
    public void validationEndBalanceFailureTest(){
        TransactionRecord txn = new TransactionRecord();
        txn.setStartBalance("15.25");
        txn.setMutation("-10.25");
        txn.setEndBalance("25.50");
        boolean outcome = transactionRecordValidationUtil.validateEndBalance(txn);
        Assert.assertTrue(outcome);
    }

    @Test(expected = DuplicateReferenceException.class)
    public void processErrorRecordsWithDuplicateRef(){
        List<TransactionRecord> reports = new ArrayList<>();
        TransactionRecord report1 = new TransactionRecord();
        report1.setReference("112806");
        List<String> failureReason = new ArrayList<>();
        failureReason.add("DUPLICATE_REFERENCE");
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TransactionRecord report2 = new TransactionRecord();
        report2.setReference("112806");
        report2.setFailureReason(failureReason);
        reports.add(report2);
        TransactionRecordValidationUtil.processErrorRecords(reports);
    }


    @Test(expected = IncorrectEndBalanceException.class)
    public void processErrorRecordsWithIncorrectBalance(){
        List<TransactionRecord> reports = new ArrayList<>();
        TransactionRecord report1 = new TransactionRecord();
        List<String> failureReason = new ArrayList<>();
        failureReason.add("BALANCE_MISMATCHED");
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TransactionRecord report2 = new TransactionRecord();
        report2.setFailureReason(failureReason);
        reports.add(report2);
        TransactionRecordValidationUtil.processErrorRecords(reports);
    }

    @Test(expected = DuplicateRefAndBalanceMismatchException.class)
    public void processErrorRecordsWithIncorrectBalanceAndDuplicateRef(){
        List<String> failureReasonBalMis = new ArrayList<>();
        failureReasonBalMis.add("BALANCE_MISMATCHED");
        List<String> failureReasonDupRef = new ArrayList<>();
        failureReasonDupRef.add("DUPLICATE_REFERENCE");
        List<TransactionRecord> reports = new ArrayList<>();
        TransactionRecord report1 = new TransactionRecord();
        report1.setFailureReason(failureReasonBalMis);
        reports.add(report1);
        TransactionRecord report2 = new TransactionRecord();
        report2.setFailureReason(failureReasonDupRef);
        reports.add(report2);
        TransactionRecordValidationUtil.processErrorRecords(reports);
    }

    @Test(expected = DuplicateRefAndBalanceMismatchException.class)
    public void processErrorRecordsWithIncorrectBalanceAndDuplicateRefInSingleCustomerRecord(){
        List<String> failureReason = new ArrayList<>();
        failureReason.add("BALANCE_MISMATCHED");
        failureReason.add("DUPLICATE_REFERENCE");
        List<TransactionRecord> reports = new ArrayList<>();
        TransactionRecord report1 = new TransactionRecord();
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TransactionRecordValidationUtil.processErrorRecords(reports);
    }


}
