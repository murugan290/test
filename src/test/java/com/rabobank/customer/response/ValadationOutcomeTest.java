package com.rabobank.customer.response;

import com.rabobank.customer.model.TxnRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * @author - Murugan Rajendran
 *
 */


@RunWith(SpringRunner.class)
public class ValadationOutcomeTest {


    @Test
    public void testValidationOutcome(){
        List<TxnRecord> reports = new ArrayList<>();
        TxnRecord report1 = new TxnRecord();
        report1.setReference("194261");
        reports.add(report1);

        TxnRecord report2 = new TxnRecord();
        report2.setReference("112806");
        reports.add(report2);

        ValidationOutcome validationOutcome = new ValidationOutcome("ValidationOutcomeTest",reports);
        Assert.assertNotEquals(null, validationOutcome);
        Assert.assertNotEquals(null, validationOutcome.toString());
        assertEquals("ValidationOutcomeTest", validationOutcome.getMessage());

        List<TxnRecord> result = validationOutcome.getErrorRecords();
        assertEquals(2, result.size());
        assertEquals("194261", result.get(0).getReference());
        assertEquals("112806", result.get(1).getReference());

    }


}
