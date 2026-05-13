package com.ttip.mesa_agil.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long resourceId) {
        super("Order with id " + resourceId + " doesn't exist");
    }
}
