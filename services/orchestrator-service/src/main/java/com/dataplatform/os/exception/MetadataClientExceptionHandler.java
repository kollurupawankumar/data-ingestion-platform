package com.dataplatform.os.exception;

import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class MetadataClientExceptionHandler {

    public RuntimeException handle(FeignException ex) {

        if (ex.status() == 404) {
            return new RuntimeException("Dataset not found");
        }

        if (ex.status() == 400) {
            return new RuntimeException("Dataset disabled or invalid");
        }

        return new RuntimeException("Metadata service unavailable");
    }
}

