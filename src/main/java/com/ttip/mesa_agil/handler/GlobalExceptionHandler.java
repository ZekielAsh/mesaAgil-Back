package com.ttip.mesa_agil.handler;

import com.ttip.mesa_agil.exception.CategoryNotEmptyException;
import com.ttip.mesa_agil.exception.OrderClosedException;
import com.ttip.mesa_agil.exception.OrderNotFoundException;
import com.ttip.mesa_agil.exception.TableAlreadyHasOpenOrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("Resource not found", HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(TableAlreadyHasOpenOrderException.class)
    public ResponseEntity<ApiError> handleTableAlreadyHasOpenOrder(TableAlreadyHasOpenOrderException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.of("The table already has an open order", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(OrderClosedException.class)
    public ResponseEntity<ApiError> handleOrderClosed(OrderClosedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("Cannot modify a closed order", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(CategoryNotEmptyException.class)
    public ResponseEntity<ApiError> handleCategoryNotEmpty(CategoryNotEmptyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("Cannot delete a non empty category", HttpStatus.CONFLICT));
    }
}
