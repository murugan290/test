package com.rabobank.customer.Utils;

import com.rabobank.customer.exception.*;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.utils.TxnRecordValidationUtil;
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
public class TxnRecordValidationUtilTest {

    @Test(expected = InvalidFileException.class)
    public void validateForEmptyInputFileTest() throws Exception{
        File jsonFile= new File(this.getClass().getResource("/records_empty.json").getFile());
        InputStream is = new FileInputStream(jsonFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", null, null, is);
        TxnRecordValidationUtil.validateInputFile(multipartFile,multipartFile.getContentType());
    }

    @Test(expected = UnsupportedFileFormatException.class)
    public void validateForUnsupportedInputFileTest() throws Exception{
        File txt= new File(this.getClass().getResource("/records.txt").getFile());
        InputStream is = new FileInputStream(txt);
        MockMultipartFile multipartFile = new MockMultipartFile("txt", null, null, is);
        TxnRecordValidationUtil.validateInputFile(multipartFile,"text");
    }

    @Test
    public void validationEndBalanceSuccessTest(){
        TxnRecord txn = new TxnRecord();
        txn.setStartBalance("15.25");
        txn.setMutation("10.25");
        txn.setEndBalance("25.50");
        boolean outcome = TxnRecordValidationUtil.validateEndBalance(txn);
        Assert.assertFalse(outcome);
    }

    @Test
    public void validationEndBalanceFailureTest(){
        TxnRecord txn = new TxnRecord();
        txn.setStartBalance("15.25");
        txn.setMutation("-10.25");
        txn.setEndBalance("25.50");
        boolean outcome = TxnRecordValidationUtil.validateEndBalance(txn);
        Assert.assertTrue(outcome);
    }

    @Test(expected = DuplicateReferenceException.class)
    public void processErrorRecordsWithDuplicateRef(){
        List<TxnRecord> reports = new ArrayList<>();
        TxnRecord report1 = new TxnRecord();
        report1.setReference("112806");
        List failureReason = new ArrayList<>();
        failureReason.add("DUPLICATE_REFERENCE");
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TxnRecord report2 = new TxnRecord();
        report2.setReference("112806");
        report2.setFailureReason(failureReason);
        reports.add(report2);
        TxnRecordValidationUtil.processErrorRecords(reports);
    }


    @Test(expected = IncorrectEndBalanceException.class)
    public void processErrorRecordsWithIncorrecrtBalance(){
        List<TxnRecord> reports = new ArrayList<>();
        TxnRecord report1 = new TxnRecord();
        List failureReason = new ArrayList<>();
        failureReason.add("BALANCE_MISMATCHED");
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TxnRecord report2 = new TxnRecord();
        report2.setFailureReason(failureReason);
        reports.add(report2);
        TxnRecordValidationUtil.processErrorRecords(reports);
    }

    @Test(expected = DuplicateRefAndBalanceMismatchException.class)
    public void processErrorRecordsWithIncorrectBalanceAndDuplicateRef(){
        List failureReasonBalMis = new ArrayList<>();
        failureReasonBalMis.add("BALANCE_MISMATCHED");
        List failureReasonDupRef = new ArrayList<>();
        failureReasonDupRef.add("DUPLICATE_REFERENCE");
        List<TxnRecord> reports = new ArrayList<>();
        TxnRecord report1 = new TxnRecord();
        report1.setFailureReason(failureReasonBalMis);
        reports.add(report1);
        TxnRecord report2 = new TxnRecord();
        report2.setFailureReason(failureReasonDupRef);
        reports.add(report2);
        TxnRecordValidationUtil.processErrorRecords(reports);
    }

    @Test(expected = DuplicateRefAndBalanceMismatchException.class)
    public void processErrorRecordsWithIncorrectBalanceAndDuplicateRefInSingleCustomerRecord(){
        List failureReason = new ArrayList<>();
        failureReason.add("BALANCE_MISMATCHED");
        failureReason.add("DUPLICATE_REFERENCE");
        List<TxnRecord> reports = new ArrayList<>();
        TxnRecord report1 = new TxnRecord();
        report1.setFailureReason(failureReason);
        reports.add(report1);
        TxnRecordValidationUtil.processErrorRecords(reports);
    }


}
