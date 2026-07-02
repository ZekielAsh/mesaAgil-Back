package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.dto.websocket.WebSocketEvent;
import com.ttip.mesa_agil.service.OrderService;
import com.ttip.mesa_agil.service.WebSocketNotificationService;
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
    private final WebSocketNotificationService notificationService;

    public OrderController(OrderService orderService, WebSocketNotificationService notificationService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable @Min(1) Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PreAuthorize("hasRole('STAFF')")
    @PatchMapping("/{orderId}/close")
    public ResponseEntity<Void> closeOrder(@PathVariable @Min(1) Long orderId) {
        OrderResponse orderResponse = orderService.closeOrderById(orderId);
        notificationService.send(
                "/room/order/" + orderResponse.id(),
                new WebSocketEvent("ORDER_CLOSED", orderId)
        );
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrderRequestBill(@PathVariable @Min(1) Long orderId) {
        orderService.cancelRequestBill(orderId);
        notificationService.send(
                "/room/order/" + orderId,
                new WebSocketEvent("ORDER_REOPEN", orderId)
        );
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/bill-requests")
    public ResponseEntity<List<OrderResponse>> getBillRequests() {
        return ResponseEntity.ok(orderService.getBillRequestsForCurrentStaff());
    }

    @PatchMapping("/{id}/request-bill")
    public ResponseEntity<Void> requestBill(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.requestBill(id);
        notificationService.send(
                "/room/staff/" + orderResponse.assignedStaffUsername(),
                new WebSocketEvent("BILL_REQUESTED",orderResponse));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderResponse> addItems(@PathVariable @Min(1) Long orderId,
                                                  @Valid @RequestBody CreateOrderItemsRequest createOrderItemsRequest) {
        OrderResponse orderResponse = orderService.addItems(orderId, createOrderItemsRequest);
        notificationService.send(
                "/room/kitchen",
                new WebSocketEvent(
                        "ORDER_ITEMS_ADDED",
                        orderResponse
                )
        );
        return ResponseEntity.ok(orderResponse);
    }

}
