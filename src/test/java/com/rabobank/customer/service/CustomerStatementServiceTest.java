package com.rabobank.customer.service;


import com.rabobank.customer.data.CustomerStatementServiceTestData;
import com.rabobank.customer.exception.FileParsingException;
import com.rabobank.customer.model.TransactionRecord;
import com.rabobank.customer.utils.TransactionRecordValidationUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.mock.web.MockMultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import static org.junit.Assert.assertEquals;


/**
 * @author - Murugan Rajendran
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TransactionRecordValidationUtil.class})
public class CustomerStatementServiceTest {

    @InjectMocks
    CustomerStatementService customerStatementService;

    @Test
    public void parseJsonFileTest() throws Exception{
        File jsonFile= new File(this.getClass().getResource("/records_success.json").getFile());
        InputStream is = new FileInputStream(jsonFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", null, null, is);
        List<TransactionRecord> myRecords = Whitebox.invokeMethod(customerStatementService,"parseInputFile",multipartFile);
        Assert.assertNotEquals(null, myRecords);
        assertEquals(10, myRecords.size());

    }

    @Test(expected = FileParsingException.class)
    public void processTransactionRecordsTest() throws Exception{
        File csvFile = new File(this.getClass().getResource("/records_parsingerror.json").getFile());
        InputStream is = new FileInputStream(csvFile);
        MockMultipartFile multipartFile = new MockMultipartFile("json", "records_parsingerror.json", "application/json", is);
        is.close();
       customerStatementService.processTransactionRecords(multipartFile);
    }

    @Test
    public void duplicateReferenceTest() throws Exception {
        CustomerStatementServiceTestData txnData = new CustomerStatementServiceTestData();
        List<TransactionRecord> records =txnData.getDuplicationReferenceDataSet();
        PowerMockito.mockStatic(TransactionRecordValidationUtil.class);
        PowerMockito.doNothing().when(TransactionRecordValidationUtil.class);
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        List<TransactionRecord> duplicateRefRecords = Whitebox.invokeMethod(customerStatementService,"applyBusinessRules",records);
        PowerMockito.verifyStatic(TransactionRecordValidationUtil.class,Mockito.times(1));
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        Assert.assertNotEquals(null, duplicateRefRecords);
        assertEquals(2, duplicateRefRecords.size());
        assertEquals("112806",duplicateRefRecords.get(0).getReference());
        assertEquals("112806",duplicateRefRecords.get(1).getReference());
        assertEquals("DUPLICATE_REFERENCE", duplicateRefRecords.get(0).getFailureReason().get(0));
        assertEquals("DUPLICATE_REFERENCE", duplicateRefRecords.get(1).getFailureReason().get(0));
    }

    @Test
    public void incorrectEndBalanceTest() throws Exception{
        CustomerStatementServiceTestData txnData = new CustomerStatementServiceTestData();
        List<TransactionRecord> records =txnData.getIncorrectBalanceDataSet();
        PowerMockito.spy(TransactionRecordValidationUtil.class);
        PowerMockito.doNothing().when(TransactionRecordValidationUtil.class);
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        List<TransactionRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"applyBusinessRules",records);
        PowerMockito.verifyStatic(TransactionRecordValidationUtil.class,Mockito.times(1));
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        Assert.assertNotEquals(null, errorRecords);
        assertEquals(2, errorRecords.size());
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(0).getFailureReason().get(0));
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(1).getFailureReason().get(0));
    }

    @Test
    public void jsonFileWithAllValidDataTest() throws Exception{
        CustomerStatementServiceTestData txnData = new CustomerStatementServiceTestData();
        List<TransactionRecord> records =txnData.getAllValidDataSet();
        List<TransactionRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"applyBusinessRules",records);
        Assert.assertNotEquals(null, errorRecords);
        assertEquals(0, errorRecords.size());
    }

    @Test
    public void duplicateReferenceAndIncorrectBalanceInsameCustomerRecordTest() throws Exception{
        CustomerStatementServiceTestData txnData = new CustomerStatementServiceTestData();
        List<TransactionRecord> records =txnData.getDuplicateRefAndIncorrentBalDataset();
        PowerMockito.spy(TransactionRecordValidationUtil.class);
        PowerMockito.doNothing().when(TransactionRecordValidationUtil.class);
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        List<TransactionRecord> errorRecords = Whitebox.invokeMethod(customerStatementService,"applyBusinessRules",records);
        PowerMockito.verifyStatic(TransactionRecordValidationUtil.class,Mockito.times(1));
        TransactionRecordValidationUtil.processErrorRecords(Mockito.any(List.class));
        Assert.assertNotEquals(null, errorRecords);
        assertEquals(2, errorRecords.size());
        assertEquals("194261",errorRecords.get(0).getReference());
        assertEquals(2,errorRecords.get(1).getFailureReason().size());
        assertEquals("DUPLICATE_REFERENCE", errorRecords.get(1).getFailureReason().get(0));
        assertEquals("BALANCE_MISMATCHED", errorRecords.get(1).getFailureReason().get(1));
    }

}
