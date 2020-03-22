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
public class DuplicateReferenceExceptionTest {

    @Test
    public void testDuplicateReferenceException() {
        List<TransactionRecord> recordDetails = new ArrayList<>();
        TransactionRecord recordDetail = new TransactionRecord();
        recordDetail.setReference("177666");
        List<String> failureReasons = new ArrayList<String>();
        failureReasons.add(Constants.DUPLICATE_REFERENCE);
        recordDetail.setFailureReason(failureReasons);
        recordDetails.add(recordDetail);

        DuplicateReferenceException duplicateRefException = new DuplicateReferenceException(200,recordDetails, Constants.DUPLICATE_REFERENCE);
        Assert.assertNotEquals(null, duplicateRefException);
        assertEquals(200, duplicateRefException.getStatusCode());
        assertEquals(Constants.DUPLICATE_REFERENCE, duplicateRefException.getMessage());

        assertEquals(1, duplicateRefException.getFailedRecords().size());
        TransactionRecord failedRecord = duplicateRefException.getFailedRecords().get(0);
        assertEquals(Constants.DUPLICATE_REFERENCE, failedRecord.getFailureReason().get(0));

    }
}
