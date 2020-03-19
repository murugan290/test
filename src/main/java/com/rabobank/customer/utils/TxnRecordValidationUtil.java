package com.rabobank.customer.utils;


import com.rabobank.customer.exception.InvalidFileException;
import com.rabobank.customer.exception.UnsupportedFileFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;



public class TxnRecordValidationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(TxnRecordValidationUtil.class);

    public static void validateInputFile(MultipartFile file, String fileType) {
        // Check for empty file
        if(file.isEmpty()){
            throw new InvalidFileException(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Empty File not allowed!!!");
        }
        if(!fileType.equalsIgnoreCase( "application/json" )){
            throw new UnsupportedFileFormatException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    String.join(" ", "The given file format",fileType,"is not supported"));
        }
    }

}
