package com.ttip.mesa_agil.dto.responses;

import com.ttip.mesa_agil.model.enums.OrderItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
        Long id,
        Long orderId,
        int tableNumber,
        ItemResponse item,
        int quantity,
        BigDecimal price,
        OrderItemStatus status,
        LocalDateTime createdAt
) { }
