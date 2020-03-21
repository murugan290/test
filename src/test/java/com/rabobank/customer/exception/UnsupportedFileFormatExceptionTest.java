package com.rabobank.customer.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class UnsupportedFileFormatExceptionTest {

    @Test
    public void TestEmptyFileException(){
        UnsupportedFileFormatException unSupportedFileException = new UnsupportedFileFormatException(500,"INTERNAL_SERVER_ERROR");
        Assert.assertNotEquals(null, unSupportedFileException);
        assertEquals(500, unSupportedFileException.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", unSupportedFileException.getMessage());
    }
}
