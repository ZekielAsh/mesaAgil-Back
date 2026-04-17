package com.ttip.mesa_agil.dto;

import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.Order;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        List<OrderItem> items,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTable().getId(),
                order.getItems(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getClosedAt()
        );
    }
}
