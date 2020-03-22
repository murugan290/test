package com.rabobank.customer.exception;


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
public class InvalidFileExceptionTest {

    @Test
    public void TestEmptyFileException(){
        InvalidFileException emptyFileException = new InvalidFileException(500,"Empty file not allowed");
        Assert.assertNotEquals(null, emptyFileException);
        assertEquals(500, emptyFileException.getStatusCode());
        assertEquals("Empty file not allowed", emptyFileException.getMessage());
    }

}
