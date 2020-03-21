package com.rabobank.customer.exception;

import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.model.TxnRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class DuplicateRefAndBalanceMismatchExceptionTest {

    @Test
    public void testDuplicateRefAndBalanceMismatchException() {
        List<TxnRecord> recordDetails = new ArrayList<>();
        TxnRecord recordDetail = new TxnRecord();
        recordDetail.setReference("177666");
        List<String> failureReasons = new ArrayList<String>();
        failureReasons.add(Constants.DUPLICATE_REFERENCE);
        failureReasons.add(Constants.BALANCE_MISMATCHED);
        recordDetail.setFailureReason(failureReasons);
        recordDetails.add(recordDetail);

        DuplicateRefAndBalanceMismatchException incorrectEndBalanceException = new DuplicateRefAndBalanceMismatchException(200,recordDetails, Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE);
        Assert.assertNotEquals(null, incorrectEndBalanceException);
        assertEquals(200, incorrectEndBalanceException.getStatusCode());
        assertEquals(Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE, incorrectEndBalanceException.getMessage());

        assertEquals(1, incorrectEndBalanceException.getFailedRecords().size());
        TxnRecord failedRecord = incorrectEndBalanceException.getFailedRecords().get(0);
        assertEquals(2, failedRecord.getFailureReason().size());
        assertEquals(Constants.DUPLICATE_REFERENCE, failedRecord.getFailureReason().get(0));
        assertEquals(Constants.BALANCE_MISMATCHED, failedRecord.getFailureReason().get(1));
    }
}
