package com.rabobank.customer.exception;

import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.model.TransactionRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class IncorrectEndBalanceExceptionTest {

    @Test
    public void testIncorrectEndBalanceException(){
        List<TransactionRecord> recordDetails = new ArrayList<>();

        TransactionRecord recordDetail = new TransactionRecord();
        recordDetail.setReference("177666");
        List<String> failureReasons = new ArrayList<String>();
        failureReasons.add("BALANCE_MISMATCHED");
        recordDetail.setFailureReason(failureReasons);
        recordDetails.add(recordDetail);

        IncorrectEndBalanceException incorrectEndBalanceException = new IncorrectEndBalanceException(200,recordDetails, Constants.INCORRECT_END_BALANCE);
        Assert.assertNotEquals(null, incorrectEndBalanceException);
        assertEquals(200, incorrectEndBalanceException.getStatusCode());
        assertEquals(Constants.INCORRECT_END_BALANCE, incorrectEndBalanceException.getMessage());

        assertEquals(1, incorrectEndBalanceException.getFailedRecords().size());
        TransactionRecord failedRecord = incorrectEndBalanceException.getFailedRecords().get(0);
        assertEquals(Constants.BALANCE_MISMATCHED, failedRecord.getFailureReason().get(0));
    }

}
