package com.ttip.mesa_agil.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        List<OrderItemResponse> orderItems,
        String status,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) { }
