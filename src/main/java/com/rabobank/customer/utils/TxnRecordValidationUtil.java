package com.rabobank.customer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabobank.customer.model.TxnRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class TxnRecordValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxnRecordValidationUtil.class);

    public static void validateInputFile(MultipartFile file, String fileType) {
        // Check for empty file
        if(file.isEmpty()){
            LOGGER.info("File is empty....");
            //throw new EmptyFileException(CustomHttpStatusCodes.HTTP_STATUS_233,ErrorMessages.FILE_CANT_BE_EMPTY);
        }
        if(fileType.equalsIgnoreCase( "application/json" )){
            //LOGGER.info("File Name " + fileName + " | " + file.getContentType() + " , ");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                TxnRecord[] detail = objectMapper.readValue(file.getInputStream(), TxnRecord[].class);
                //LOGGER.info("details " + detail.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            LOGGER.info("Unsupported file....");
        }


    }

}
