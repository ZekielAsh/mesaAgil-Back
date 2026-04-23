package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.CreateOrderRequest;
import com.ttip.mesa_agil.dto.OrderResponse;
import com.ttip.mesa_agil.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PostMapping("/{orderId}/close")
    public ResponseEntity<OrderResponse> closeOrder(@PathVariable Long orderId) {
        orderService.closeOrderById(orderId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/table/{tableId}")
    public ResponseEntity<OrderResponse> create(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.create(new CreateOrderRequest(tableId)));
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItems(@PathVariable Long orderId,
                                                  @RequestBody CreateOrderItemsRequest createOrderItemsRequest) {
        return ResponseEntity.ok(orderService.addItems(orderId, createOrderItemsRequest));
    }

}
