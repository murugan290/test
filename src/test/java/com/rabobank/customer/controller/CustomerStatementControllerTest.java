package com.rabobank.customer.controller;


import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.exception.InvalidFileException;
import com.rabobank.customer.exception.UnsupportedFileFormatException;
import com.rabobank.customer.model.TransactionRecord;
import com.rabobank.customer.response.CustomerValidationResult;
import com.rabobank.customer.service.CustomerStatementService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @author - Murugan Rajendran
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CustomerStatementControllerTest {

    @InjectMocks
    CustomerStatementController customerStatementController;

    @Mock
    CustomerStatementService customerStatementService;

    @Test
    public void jsonFileWithGivenData() throws Exception{
        String message = "SUCCESSFUL";
        File jsonFile = new File(this.getClass().getResource("/records_success.json").getFile());
        InputStream is = new FileInputStream(jsonFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "records_success.json", "application/json", is);
        is.close();
        List<TransactionRecord> reports = new ArrayList<>();
        Mockito.when(customerStatementService.processTransactionRecords(multipartFile)).thenReturn(reports);
        ResponseEntity<CustomerValidationResult> result = customerStatementController.processInputFile(multipartFile);
        Assert.assertNotEquals(null, result);
        Assert.assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
        assertEquals(message, result.getBody().getMessage());
        verify(customerStatementService, times(1)).processTransactionRecords(multipartFile);

    }

    @Test(expected = UnsupportedFileFormatException.class)
    public void unsupportedFileFormatWithTextFile() throws Exception {
        File textFile = new File(this.getClass().getResource("/records.txt").getFile());
        InputStream is = new FileInputStream(textFile);
        MockMultipartFile multipartFile = new MockMultipartFile("txt", "records.txt", "text/plain", is);
        is.close();
        Mockito.when(customerStatementService.processTransactionRecords(multipartFile)).thenThrow(new UnsupportedFileFormatException(500,"The given file format txt is not supported"));
        customerStatementController.processInputFile(multipartFile);
        verify(customerStatementService, times(1)).processTransactionRecords(multipartFile);
    }

    @Test(expected = InvalidFileException.class)
    public void emptyJsonFile() throws Exception {
        File textFile = new File(this.getClass().getResource("/records_empty.json").getFile());
        InputStream is = new FileInputStream(textFile);
        MockMultipartFile multipartFile = new MockMultipartFile("txt", "records.txt", "text/plain", is);
        is.close();
        Mockito.when(customerStatementService.processTransactionRecords(multipartFile)).thenThrow(new InvalidFileException(500,"Empty File not allowed"));
        customerStatementController.processInputFile(multipartFile);
        verify(customerStatementService, times(1)).processTransactionRecords(multipartFile);
    }

    @Test(expected = FileParsingException.class)
    public void jsonFileWithInvalidContent() throws Exception {
        File textFile = new File(this.getClass().getResource("/records_empty.json").getFile());
        InputStream is = new FileInputStream(textFile);
        MockMultipartFile multipartFile = new MockMultipartFile("txt", "records.txt", "text/plain", is);
        is.close();
        Mockito.when(customerStatementService.processTransactionRecords(multipartFile)).thenThrow(new FileParsingException(400,"BAD_REQUEST"));
        customerStatementController.processInputFile(multipartFile);
        verify(customerStatementService, times(1)).processTransactionRecords(multipartFile);
    }


}
