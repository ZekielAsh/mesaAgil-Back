package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable @Min(1) Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasRole('STAFF')")
    @PatchMapping("/{orderId}/close")
    public ResponseEntity<Void> closeOrder(@PathVariable @Min(1) Long orderId) {
        orderService.closeOrderById(orderId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/bill-requests")
    public ResponseEntity<List<OrderResponse>> getBillRequests() {
        return ResponseEntity.ok(orderService.getBillRequests());
    }

    @PatchMapping("/{id}/request-bill")
    public ResponseEntity<Void> requestBill(@PathVariable Long id) {
        orderService.requestBill(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItems(@PathVariable @Min(1) Long orderId,
                                                  @Valid @RequestBody CreateOrderItemsRequest createOrderItemsRequest) {
        return ResponseEntity.ok(orderService.addItems(orderId, createOrderItemsRequest));
    }

}
