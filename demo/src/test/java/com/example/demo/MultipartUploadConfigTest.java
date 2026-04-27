package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.unit.DataSize;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MultipartUploadConfigTest {

    @Autowired
    private MultipartProperties multipartProperties;

    @Test
    void menuImageUploadsAllowFilesUpToFifteenMegabytes() {
        assertEquals(DataSize.ofMegabytes(15), multipartProperties.getMaxFileSize());
        assertEquals(DataSize.ofMegabytes(15), multipartProperties.getMaxRequestSize());
    }
}
