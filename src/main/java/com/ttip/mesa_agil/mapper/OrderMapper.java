package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;

import java.util.List;

public class OrderMapper {
    public static OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemMapper::toResponse)
                .toList();

        System.out.println(order.getTable().getId());

        System.out.println(
                order.getTable().getAssignedStaff() == null ?
                        "NO STAFF" :
                        order.getTable().getAssignedStaff().getUsername()
        );

        return new OrderResponse(
                order.getId(),
                order.getTable().getId(),
                order.getTable().getAssignedStaff().getUsername(),
                items,
                order.getStatus().name(),
                order.isBillRequested(),
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
