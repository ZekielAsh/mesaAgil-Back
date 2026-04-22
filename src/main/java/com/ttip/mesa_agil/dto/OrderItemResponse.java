package com.ttip.mesa_agil.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
        Long id,
        Long orderId,
        ItemResponse item,
        int quantity,
        BigDecimal price,
        String status,
        LocalDateTime createdAt
) { }
