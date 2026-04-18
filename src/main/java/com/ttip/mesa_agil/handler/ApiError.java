package com.ttip.mesa_agil.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiError {

    private String message;
    private int status;
    private Object details;

    public static ApiError of(String message, HttpStatus status) {
        return new ApiError(message, status.value(), null);
    }

    public static ApiError of(String message, Object details) {
        return new ApiError(message, HttpStatus.BAD_REQUEST.value(), details);
    }

}
