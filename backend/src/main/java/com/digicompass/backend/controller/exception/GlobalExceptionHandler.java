package com.digicompass.backend.controller.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("[CONTROLLER] JSON Processing error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing weather data");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("[CONTROLLER] Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("[CONTROLLER] Illegal Argument exception error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        log.error("[CONTROLLER] IO error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("[CONTROLLER] No such element error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No such element server error");
    }

//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<String> handleEntityNotFoundException(NoSuchElementException ex) {
//        log.error("[CONTROLLER] Entity not found error: ", ex);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body("Entity not found error");
//    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParseException(DateTimeParseException ex) {
        log.error("[CONTROLLER] Date Time Parse error: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)  // test it with date of trip creation
                .body("Date Time Parse error");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleDateHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("[CONTROLLER] Http Message Not Readable error: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)  // send different type of id different than long, trip creation
                .body("Http Message Not Readable error");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("[CONTROLLER] Access denied error: ", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied error");
    }


}

