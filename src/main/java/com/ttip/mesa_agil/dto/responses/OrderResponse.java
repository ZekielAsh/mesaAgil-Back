package com.ttip.mesa_agil.dto.responses;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        String assignedStaffUsername,
        List<OrderItemResponse> orderItems,
        String status,
        Boolean billRequested,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) { }
