package com.digicompass.backend.controller.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("JSON Processing error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing weather data");
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal Argument exception error: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Internal server error");
    }
}

