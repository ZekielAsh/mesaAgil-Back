package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.OrderItemResponse;
import com.ttip.mesa_agil.model.OrderItem;

public class OrderItemMapper {
    public static OrderItemResponse toResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                ItemMapper.toResponse(orderItem.getItem()),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getStatus().name(),
                orderItem.getCreatedAt()
        );
    }
}
