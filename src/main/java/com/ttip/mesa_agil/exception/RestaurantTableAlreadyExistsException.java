package com.ttip.mesa_agil.exception;

public class RestaurantTableAlreadyExistsException extends RuntimeException {
    public RestaurantTableAlreadyExistsException(Integer number) {
        super("Restaurant table already exists for number=" + number);
    }
}
