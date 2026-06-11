package com.ttip.mesa_agil.dto.responses;

public record TableSessionResponse(
        Long tableId,
        Integer tableNumber,
        Boolean enabled,
        String qrToken,
        Long sessionId,
        Long orderId,
        Boolean active
) {
}
