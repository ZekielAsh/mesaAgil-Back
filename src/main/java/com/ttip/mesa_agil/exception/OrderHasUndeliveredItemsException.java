package com.ttip.mesa_agil.exception;

public class OrderHasUndeliveredItemsException
        extends RuntimeException {

    public OrderHasUndeliveredItemsException(String message) {
        super(message);
    }
}
