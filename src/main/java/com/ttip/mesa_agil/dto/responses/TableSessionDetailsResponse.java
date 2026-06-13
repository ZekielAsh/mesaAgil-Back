package com.ttip.mesa_agil.dto.responses;

import java.time.LocalDateTime;

public record TableSessionDetailsResponse(
        Long sessionId,
        Long tableId,
        Integer tableNumber,
        Integer customerCount,
        Boolean active,
        LocalDateTime startedAt,
        LocalDateTime endedAt
) {
}
