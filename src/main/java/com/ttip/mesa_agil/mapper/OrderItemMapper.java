package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.model.OrderItem;

import java.util.List;

public class OrderItemMapper {
    public static OrderItemResponse toResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getOrder().getId(),
                orderItem.getOrder().getTable().getNumber(),
                ItemMapper.toResponse(orderItem.getItem()),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getStatus(),
                orderItem.getCreatedAt()
        );
    }

    public static List<OrderItemResponse> toResponseList(List<OrderItem> orderItemList) {
        return orderItemList.stream()
                .map(OrderItemMapper::toResponse)
                .toList();
    }
}
