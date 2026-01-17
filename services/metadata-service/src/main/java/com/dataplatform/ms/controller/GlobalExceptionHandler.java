package com.dataplatform.ms.controller;

import com.dataplatform.ms.exception.InvalidPipelineException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPipelineException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPipeline(
            InvalidPipelineException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}
