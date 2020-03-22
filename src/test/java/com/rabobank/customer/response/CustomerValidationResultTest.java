package com.rabobank.customer.response;

import com.rabobank.customer.model.TransactionRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;


/**
 * @author - Murugan Rajendran
 *
 */


@RunWith(SpringRunner.class)
public class CustomerValidationResultTest {


    @Test
    public void testValidationOutcome(){
        List<TransactionRecord> reports = new ArrayList<>();
        TransactionRecord report1 = new TransactionRecord();
        report1.setReference("194261");
        reports.add(report1);

        TransactionRecord report2 = new TransactionRecord();
        report2.setReference("112806");
        reports.add(report2);

        CustomerValidationResult customerValidationResult = new CustomerValidationResult("ValidationOutcomeTest",reports);
        Assert.assertNotEquals(null, customerValidationResult);
        Assert.assertNotEquals(null, customerValidationResult.toString());
        assertEquals("ValidationOutcomeTest", customerValidationResult.getMessage());

        List<TransactionRecord> result = customerValidationResult.getErrorRecords();
        assertEquals(2, result.size());
        assertEquals("194261", result.get(0).getReference());
        assertEquals("112806", result.get(1).getReference());

    }


}
