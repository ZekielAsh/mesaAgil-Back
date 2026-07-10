package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.BillItemResponse;
import com.ttip.mesa_agil.dto.responses.BillSummaryResponse;
import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.RestaurantTable;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {
    public static OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems().stream()
                .map(OrderItemMapper::toResponse)
                .toList();

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

    public static BillSummaryResponse toBillSummary(Order order) {

        List<BillItemResponse> items = order.getItems().stream()
                .map(OrderMapper::toBillItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(BillItemResponse::totalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BillSummaryResponse(
                order.getId(),
                order.getTable().getNumber(),
                order.getCreatedAt(),
                items,
                total
        );
    }

    private static BillItemResponse toBillItemResponse(OrderItem item) {

        BigDecimal totalPrice = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new BillItemResponse(
                item.getItem().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                totalPrice
        );
    }
}
