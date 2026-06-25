package com.ttip.mesa_agil.dto;

public record CloseSessionResult(
        Long tableId,
        Long cancelledOrderId
) {
}