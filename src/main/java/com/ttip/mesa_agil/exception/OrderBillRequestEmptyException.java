package com.ttip.mesa_agil.exception;

public class OrderBillRequestEmptyException extends IllegalStateException {
    public OrderBillRequestEmptyException(String message) {
        super(message);
    }
}
