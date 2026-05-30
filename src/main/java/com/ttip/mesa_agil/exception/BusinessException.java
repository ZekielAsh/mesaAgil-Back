package com.ttip.mesa_agil.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) { super(message); }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
