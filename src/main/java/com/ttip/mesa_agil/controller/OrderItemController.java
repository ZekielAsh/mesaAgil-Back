package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.UpdateOrderItemRequest;
import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
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
    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getKitchenOrderItems() {
        return ResponseEntity.ok(orderItemService.getKitchenOrderItems());
    }

    @PreAuthorize("hasRole('KITCHEN')")
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
