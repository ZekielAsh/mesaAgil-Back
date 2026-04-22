package com.ttip.mesa_agil.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long resourceId) {
        super("Order with id " + resourceId + " doesn't exist");
    }
}
