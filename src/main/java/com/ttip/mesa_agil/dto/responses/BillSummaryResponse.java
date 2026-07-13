package com.ttip.mesa_agil.dto.responses;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BillSummaryResponse(
        Long orderId,
        Integer tableNumber,
        Instant orderedAt,
        List<BillItemResponse> items,
        BigDecimal total
) {
}