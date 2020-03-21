package com.rabobank.customer.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

/**
 * @author - Murugan Rajendran
 *
 */

@RunWith(SpringRunner.class)
public class TxnRecordTest {


    @Test
    public void testTanRecord() {
        TxnRecord recordDetail1 = new TxnRecord();
        recordDetail1.setReference("194261");
        recordDetail1.setAccountNumber("NL91RABO0315273637");
        recordDetail1.setDescription("Clothes from Jan Bakker");
        recordDetail1.setStartBalance("21.6");
        recordDetail1.setMutation("-41.83");
        recordDetail1.setEndBalance("-20.23");

        Assert.assertNotEquals(null, recordDetail1);
        Assert.assertNotEquals(null, recordDetail1.toString());
        assertEquals("194261", recordDetail1.getReference());
        assertEquals("NL91RABO0315273637", recordDetail1.getAccountNumber());
        assertEquals("Clothes from Jan Bakker", recordDetail1.getDescription());
        assertEquals("21.6", recordDetail1.getStartBalance());
        assertEquals("-41.83", recordDetail1.getMutation());
        assertEquals("-20.23", recordDetail1.getEndBalance());


    }

}
