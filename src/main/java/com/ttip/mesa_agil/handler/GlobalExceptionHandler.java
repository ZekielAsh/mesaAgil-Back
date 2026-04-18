package com.ttip.mesa_agil.handler;

import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.TableAlreadyHasOpenOrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("Resource not found", HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TableAlreadyHasOpenOrderException.class)
    public ResponseEntity<ApiError> handleTableAlreadyHasOpenOrder(TableAlreadyHasOpenOrderException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of("The table already has an open order", HttpStatus.CONFLICT));
    }
}
