package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.CloseSessionResult;
import com.ttip.mesa_agil.dto.requests.UpdateCustomerCountRequest;
import com.ttip.mesa_agil.dto.responses.TableOccupancyResponse;
import com.ttip.mesa_agil.dto.websocket.WebSocketEvent;
import com.ttip.mesa_agil.service.RestaurantTableService;
import com.ttip.mesa_agil.service.TableSessionService;
import com.ttip.mesa_agil.dto.requests.CreateTableSessionRequest;
import com.ttip.mesa_agil.dto.responses.TableSessionDetailsResponse;
import com.ttip.mesa_agil.service.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/table-sessions")
@RequiredArgsConstructor
public class TableSessionController {

    private final TableSessionService service;
    private final WebSocketNotificationService notificationService;
    private final RestaurantTableService restaurantTableService;

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/table/{tableId}")
    public ResponseEntity<TableSessionDetailsResponse> create(
            @PathVariable Long tableId,
            @RequestBody @Valid CreateTableSessionRequest request
    ) {
        TableSessionDetailsResponse response = service.createSession(tableId, request);
        TableOccupancyResponse occupancy = restaurantTableService.getOccupancyByTableId(tableId);
        notificationService.send(
                "/room/tables",
                new WebSocketEvent("ASSIGNED_TABLE_UPDATED", occupancy));
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('STAFF')")
    @PatchMapping("/table/{tableId}/close")
    public ResponseEntity<Void> close(
            @PathVariable Long tableId
    ) {
        CloseSessionResult result = service.closeSession(tableId);
        TableOccupancyResponse occupancy = restaurantTableService.getOccupancyByTableId(tableId);
        notificationService.send(
                "/room/tables",
                new WebSocketEvent("ASSIGNED_TABLE_UPDATED", occupancy));

        if (result.cancelledOrderId() != null) {
            System.out.println("here");
            notificationService.send(
                    "/room/order/" + result.cancelledOrderId(),
                    new WebSocketEvent("ORDER_CANCELLED", result.cancelledOrderId()));
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{sessionId}/customers")
    public ResponseEntity<TableSessionDetailsResponse> updateCustomers(
            @PathVariable Long sessionId,
            @RequestBody @Valid UpdateCustomerCountRequest request
    ) {
        return ResponseEntity.ok(service.updateCustomerCount(sessionId,request));
    }
}
