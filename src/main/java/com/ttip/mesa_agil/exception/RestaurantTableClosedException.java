package com.ttip.mesa_agil.exception;

public class RestaurantTableClosedException extends RuntimeException {

    public RestaurantTableClosedException(Long tableId) {
        super("Restaurant table is closed for id=" + tableId);
    }
}
