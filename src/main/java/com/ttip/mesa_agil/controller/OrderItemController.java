package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.UpdateOrderItemRequest;
import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderItems")
@Validated
public class OrderItemController {
    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PreAuthorize("hasRole('KITCHEN')")
    @GetMapping("/kitchen")
    public ResponseEntity<List<OrderItemResponse>> getKitchenOrderItems() {
        return ResponseEntity.ok(orderItemService.getOrderItemsByStatusList(List.of(
                OrderItemStatus.PENDING,
                OrderItemStatus.IN_PREPARATION
        )));
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/ready")
    public ResponseEntity<List<OrderItemResponse>> getReadyOrderItems() {
        return ResponseEntity.ok(orderItemService.getOrderItemsByStatusList(List.of(
                OrderItemStatus.READY
        )));
    }

    @PreAuthorize("hasAnyRole('KITCHEN', 'STAFF')")
    @PatchMapping("/{orderItemId}/status")
    public ResponseEntity<OrderItemResponse> updateOrderItemStatus(
            @PathVariable Long orderItemId,
            @RequestBody UpdateOrderItemRequest request
    ) {

        OrderItemResponse response = orderItemService.updateOrderItemStatus(
                orderItemId,
                request.status()
        );

        return ResponseEntity.ok(response);
    }
}
