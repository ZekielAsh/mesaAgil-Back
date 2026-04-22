package com.ttip.mesa_agil.exception;

public class OrderClosedException extends IllegalStateException {
    public OrderClosedException(Long orderId) {
        super("Order with id " + orderId + " is closed");
    }
}
