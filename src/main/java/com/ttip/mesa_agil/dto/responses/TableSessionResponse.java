package com.ttip.mesa_agil.dto.responses;

public record TableSessionResponse(
        Long tableId,
        int tableNumber,
        boolean tableEnabled,
        String qrToken,
        Long orderId,
        String orderStatus,
        boolean activeSession
) { }
