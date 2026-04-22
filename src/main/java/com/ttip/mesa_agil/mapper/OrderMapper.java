package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.OrderItemResponse;
import com.ttip.mesa_agil.dto.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;

import java.util.List;

public class OrderMapper {
    public static OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemMapper::toResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTable().getId(),
                items,
                order.getStatus().name(),
                order.getCreatedAt(),
                order.getClosedAt()
        );
    }

    public static Order toEntity(RestaurantTable table) {
        Order order = new Order();
        order.setTable(table);
        return order;
    }
}
