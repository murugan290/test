package com.rabobank.customer.exception;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
public class FileParsingExceptionTest {

    @Test
    public void testFileParsingException(){
        FileParsingException fileParsingException = new FileParsingException(400,"BAD_REQUEST");
        Assert.assertNotEquals(null, fileParsingException);
        assertEquals(400, fileParsingException.getStatusCode());
        assertEquals("BAD_REQUEST", fileParsingException.getMessage());
    }
}
