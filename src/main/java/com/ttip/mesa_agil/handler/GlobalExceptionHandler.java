package com.ttip.mesa_agil.handler;

import com.ttip.mesa_agil.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("Resource not found", HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
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

    @ExceptionHandler(RestaurantTableAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleRestaurantTableAlreadyExists(RestaurantTableAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("The table already exists", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(RestaurantTableClosedException.class)
    public ResponseEntity<ApiError> handleRestaurantTableClosed(RestaurantTableClosedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("The table is closed and does not accept new sessions", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(CategoryNotEmptyException.class)
    public ResponseEntity<ApiError> handleCategoryNotEmpty(CategoryNotEmptyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("Cannot delete a non empty category", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(v -> {
            String fullPath = v.getPropertyPath().toString();

            String field = fullPath.contains(".")
                    ? fullPath.substring(fullPath.lastIndexOf(".") + 1)
                    : fullPath;

            errors.put(field, v.getMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Validation error", errors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Validation error", errors));
    }

    @ExceptionHandler(OrderBillRequestException.class)
    public ResponseEntity<ApiError> handleOrderBillRequestException(OrderBillRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("La cuenta ya fue solicitada", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(OrderBillRequestEmptyException.class)
    public ResponseEntity<ApiError> handleOrderBillRequestEmptyException(OrderBillRequestEmptyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("No hay comidas en la orden", HttpStatus.CONFLICT));
    }

    @ExceptionHandler(OrderHasUndeliveredItemsException.class)
    public ResponseEntity<ApiError> handleOrderHasUndeliveredItemsException(
            OrderHasUndeliveredItemsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of("Hay comidas pendientes", HttpStatus.CONFLICT));
    }
}
