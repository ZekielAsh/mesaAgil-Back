package com.ttip.mesa_agil.dto;

import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull
        Long tableId
) {
    public static Order toOrder(RestaurantTable table) {
        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.OPEN);
        return order;
    }
}
