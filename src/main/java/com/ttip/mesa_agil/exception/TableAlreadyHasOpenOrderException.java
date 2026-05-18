package com.ttip.mesa_agil.exception;

public class TableAlreadyHasOpenOrderException extends RuntimeException {

    public TableAlreadyHasOpenOrderException(Long tableId) {
        super("Table already has an open order for tableId=" + tableId);
    }
}
