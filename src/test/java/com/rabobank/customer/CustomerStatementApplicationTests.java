package com.rabobank.customer;


import com.rabobank.customer.constants.Constants;
import com.rabobank.customer.controller.CustomerStatementController;
import com.rabobank.customer.response.CustomerValidationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.io.File;
import java.net.URI;
import static org.junit.Assert.*;

/**
 * @author - Murugan Rajendran
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerStatementApplicationTests {

    @Autowired
    CustomerStatementController customerStatementController;

    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    public void contextLoads() {
        assertNotNull(customerStatementController);
    }


    @Test
    public void uploadValidCustomerDataTest() throws Exception{
        File jsonFile = new File(this.getClass().getResource("/records_success.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);

        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(200, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals("SUCCESSFUL", outcome.getMessage());
        assertEquals(0,outcome.getErrorRecords().size());
    }

    @Test
    public void uploadFileWithDuplicateAndIncorrectBalanceTest() throws Exception {
        File jsonFile = new File(this.getClass().getResource("/duplicate_reference_balance_mismatch.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);

        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(200, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.DUPLICATE_REFERENCE_INCORRECT_END_BALANCE, outcome.getMessage());
        assertEquals(3,outcome.getErrorRecords().size());

        assertEquals("194261", outcome.getErrorRecords().get(0).getReference());
        assertEquals("NL91RABO0315273637", outcome.getErrorRecords().get(0).getAccountNumber());

        assertEquals("194261", outcome.getErrorRecords().get(1).getReference());
        assertEquals("NL74ABNA0248990274", outcome.getErrorRecords().get(1).getAccountNumber());

        assertEquals("112806", outcome.getErrorRecords().get(2).getReference());
        assertEquals("NL27SNSB0917829871", outcome.getErrorRecords().get(2).getAccountNumber());

        assertEquals(Constants.DUPLICATE_REFERENCE,outcome.getErrorRecords().get(0).getFailureReason().get(0));
        assertEquals(Constants.DUPLICATE_REFERENCE,outcome.getErrorRecords().get(1).getFailureReason().get(0));
        assertEquals(Constants.BALANCE_MISMATCHED,outcome.getErrorRecords().get(2).getFailureReason().get(0));
    }

    @Test
    public void uploadFileWithIncorrectBalanceTest() throws Exception {
        File jsonFile = new File(this.getClass().getResource("/records_balance_mismatch.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);

        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(200, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.INCORRECT_END_BALANCE, outcome.getMessage());
        assertEquals(2,outcome.getErrorRecords().size());

        assertEquals("112806", outcome.getErrorRecords().get(0).getReference());
        assertEquals("NL27SNSB0917829871", outcome.getErrorRecords().get(0).getAccountNumber());

        assertEquals("183049", outcome.getErrorRecords().get(1).getReference());
        assertEquals("NL69ABNA0433647324", outcome.getErrorRecords().get(1).getAccountNumber());

        assertEquals(Constants.BALANCE_MISMATCHED,outcome.getErrorRecords().get(0).getFailureReason().get(0));
        assertEquals(Constants.BALANCE_MISMATCHED,outcome.getErrorRecords().get(1).getFailureReason().get(0));
    }

    @Test
    public void uploadFileWithDuplicateReferenceTest() throws Exception {
        File jsonFile = new File(this.getClass().getResource("/records_duplicate_reference.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);

        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(200, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.DUPLICATE_REFERENCE, outcome.getMessage());
        assertEquals(2,outcome.getErrorRecords().size());

        assertEquals("112806", outcome.getErrorRecords().get(0).getReference());
        assertEquals("NL27SNSB0917829871", outcome.getErrorRecords().get(0).getAccountNumber());

        assertEquals("112806", outcome.getErrorRecords().get(0).getReference());
        assertEquals("NL74ABNA0248990274", outcome.getErrorRecords().get(1).getAccountNumber());

        assertEquals(Constants.DUPLICATE_REFERENCE,outcome.getErrorRecords().get(0).getFailureReason().get(0));
        assertEquals(Constants.DUPLICATE_REFERENCE,outcome.getErrorRecords().get(1).getFailureReason().get(0));

    }

    @Test
    public void uploadFileWithErroneousContentTest() throws Exception {
        File jsonFile = new File(this.getClass().getResource("/records_parsingerror.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);
        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(400, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.BAD_REQUEST, outcome.getMessage());
        assertEquals(0,outcome.getErrorRecords().size());
    }

    @Test
    public void uploadUnsupportedFileFormatTest() throws Exception {
        File textFile = new File(this.getClass().getResource("/records_unsupportedfile.txt").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(textFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);
        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(500, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.INTERNAL_SERVER_ERROR, outcome.getMessage());
        assertEquals(0,outcome.getErrorRecords().size());
    }

    @Test
    public void uploadEmptyCustomerDataFileTest() throws Exception{
        File jsonFile = new File(this.getClass().getResource("/records_empty.json").getFile());
        final String baseUrl = "http://localhost:" + randomServerPort+ "/statement/processCustomerStatement";
        URI uri = new URI(baseUrl);
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", new FileSystemResource(jsonFile));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(bodyMap, headers);
        ResponseEntity<CustomerValidationResult> result = restTemplate.postForEntity(uri, requestEntity, CustomerValidationResult.class);
        System.out.println("result " + result);
        assertNotEquals(null, result);
        assertNotNull(result.getStatusCodeValue());
        assertEquals(500, result.getStatusCodeValue());
        CustomerValidationResult outcome = result.getBody();
        assertNotNull(outcome);
        assertNotNull(outcome.getMessage());
        assertEquals(Constants.INTERNAL_SERVER_ERROR, outcome.getMessage());
        assertEquals(0,outcome.getErrorRecords().size());

    }


}
