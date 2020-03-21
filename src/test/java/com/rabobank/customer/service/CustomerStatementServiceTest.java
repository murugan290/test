package com.rabobank.customer.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.Data.CustomerTxnData;
import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.exception.IncorrectEndBalanceException;
import com.rabobank.customer.model.TxnRecord;
import com.rabobank.customer.utils.TxnRecordValidationUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.assertEquals;


/**
 * @author - Murugan Rajendran
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TxnRecordValidationUtil.class,CustomerStatementService.class})
public class CustomerStatementServiceTest {

    @InjectMocks
    CustomerStatementService customerStatementService;

    @Test
    public void validateCustomerTxnRecordsTest() throws Exception{
        File jsonFile= new File(this.getClass().getResource("/records_success.json").getFile());
        InputStream is = new FileInputStream(jsonFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", null, null, is);
        List<TxnRecord> records = new ArrayList<>();
        customerStatementService = PowerMockito.spy(new CustomerStatementService());
        PowerMockito.doReturn(records)
                .when(customerStatementService, "executeBusinessRules", records);
        Whitebox.invokeMethod(customerStatementService,"validateCustomerTxnRecords",multipartFile);
        Assert.assertNotEquals(null, records);
        assertEquals(0, records.size());

    }

    @Test(expected = FileParsingException.class)
    public void processTransactionRecordsTest() throws Exception{
        File csvFile = new File(this.getClass().getResource("/records_parsingerror.json").getFile());
        InputStream is = new FileInputStream(csvFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "records_parsingerror.json", "application/json", is);
        is.close();
        /*PowerMockito.mockStatic(TxnRecordValidationUtil.class);
        PowerMockito.doNothing().when(TxnRecordValidationUtil.class);
        TxnRecordValidationUtil.validateInputFile(multipartFile,multipartFile.getContentType());*/
        customerStatementService.processTransactionRecords(multipartFile);
        //Mockito.when(objectMapper.readValue(multipartFile.getInputStream(), TxnRecord[].class)).thenThrow(new FileParsingException(HttpStatus.BAD_REQUEST.value(),Constants.BAD_REQUEST ));
        //Whitebox.invokeMethod(CustomerStatementService.class,"validateCustomerTxnRecords",multipartFile);
        //PowerMockito.doReturn().when(classUnderTest, "privateApi", anyString(), anyInt());

/*        PowerMockito.doThrow(new FileParsingException(HttpStatus.BAD_REQUEST.value(),Constants.BAD_REQUEST ) )
        //        .when(customerStatementService,"validateCustomerTxnRecords",multipartFile);
        .when(customerStatementService).processTransactionRecords(multipartFile);*/

        //PowerMockito.when(customerStatementService,"validateCustomerTxnRecords",multipartFile).thenThrow(new FileParsingException(HttpStatus.BAD_REQUEST.value(),Constants.BAD_REQUEST));


        /*PowerMockito.verifyStatic(TxnRecordValidationUtil.class,times(1));
        //this is how you verify them.
        TxnRecordValidationUtil.validateInputFile(multipartFile,multipartFile.getContentType());*/


        //verify(customerStatementService, times(1)).processTransactionRecords(multipartFile);


    }

    @Test
    public void duplicateReferenceTest() throws Exception{
        CustomerTxnData txnData = new CustomerTxnData();
        List<TxnRecord> records =txnData.getDuplicationReferenceDataSet();
        //PowerMockito.doNothing().when(TxnRecordValidationUtil.class);
        //TxnRecordValidationUtil.processErrorRecords(multipartFile,multipartFile.getContentType());*/
        Set<String> referenceNumbers = Whitebox.invokeMethod(customerStatementService,"findDuplicateReferenceData",records);
        List<TxnRecord> duplicateRefRecords = Whitebox.invokeMethod(customerStatementService,"updateFailureReasonInDuplicateReferenceRecords",records,referenceNumbers);
        List<TxnRecord> incorrectEndBalance = Whitebox.invokeMethod(customerStatementService,"findEndBalanceMismatchRecords",records,referenceNumbers);
        duplicateRefRecords.addAll(incorrectEndBalance);
        Assert.assertNotEquals(null, duplicateRefRecords);
        assertEquals(2, duplicateRefRecords.size());
        assertEquals("112806",duplicateRefRecords.get(0).getReference());
        assertEquals("112806",duplicateRefRecords.get(1).getReference());
        assertEquals("DUPLICATE_REFERENCE", duplicateRefRecords.get(0).getFailureReason().get(0));
        assertEquals("DUPLICATE_REFERENCE", duplicateRefRecords.get(1).getFailureReason().get(0));
    }

    @Test
    public void incorrectEndBalanceTest() throws Exception{
        CustomerTxnData txnData = new CustomerTxnData();
        List<TxnRecord> records = txnData.getIncorrectBalanceDataSet();
        Set<String> referenceNumbers = Whitebox.invokeMethod(customerStatementService,"findDuplicateReferenceData",records);
        List<TxnRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"updateFailureReasonInDuplicateReferenceRecords",records,referenceNumbers);
        List<TxnRecord> incorrectEndBalance = Whitebox.invokeMethod(customerStatementService,"findEndBalanceMismatchRecords",records,referenceNumbers);
        errorRecords.addAll(incorrectEndBalance);
        Assert.assertNotEquals(null, errorRecords);
        assertEquals(2, errorRecords.size());
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(0).getFailureReason().get(0));
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(1).getFailureReason().get(0));

    }

    @Test
    public void jsonFileWithAllValidDataTest() throws Exception{
        CustomerTxnData txnData = new CustomerTxnData();
        List<TxnRecord> records =txnData.getAllValidDataSet();
        /*PowerMockito.spy(TxnRecordValidationUtil.class);
        PowerMockito.doNothing().when(TxnRecordValidationUtil.class,"processErrorRecords", records);
        TxnRecordValidationUtil.processErrorRecords(records);*/
        List<TxnRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"executeBusinessRules",records);
        System.out.println("Error records "+ errorRecords.size());
        Assert.assertNotEquals(null, errorRecords);
        assertEquals(0, errorRecords.size());
    }

    @Test
    public void duplicatereference_incorrectBalanceInsameCustomerRecordTest() throws Exception{
        CustomerTxnData txnData = new CustomerTxnData();
        List<TxnRecord> records =txnData.getDuplicateRefAndIncorrentBalDataset();
        Set<String> referenceNumbers = Whitebox.invokeMethod(customerStatementService,"findDuplicateReferenceData",records);
        List<TxnRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"updateFailureReasonInDuplicateReferenceRecords",records,referenceNumbers);
        List<TxnRecord> incorrectEndBalance = Whitebox.invokeMethod(customerStatementService,"findEndBalanceMismatchRecords",records,referenceNumbers);
        errorRecords.addAll(incorrectEndBalance);

        Assert.assertNotEquals(null, errorRecords);
        assertEquals(2, errorRecords.size());
        assertEquals("194261",errorRecords.get(0).getReference());
        assertEquals(2,errorRecords.get(1).getFailureReason().size());
        assertEquals("DUPLICATE_REFERENCE", errorRecords.get(1).getFailureReason().get(0));
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(1).getFailureReason().get(1));

    }



}
