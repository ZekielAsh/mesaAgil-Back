package com.ttip.mesa_agil.dto.responses;

public record TableSessionResponse(
        Long tableId,
        Integer tableNumber,
        boolean tableEnabled,
        String qrToken,
        Long sessionId,
        Long orderId,
        boolean activeSession
) {
}
