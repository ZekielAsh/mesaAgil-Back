package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.requests.UpdateCustomerCountRequest;
import com.ttip.mesa_agil.service.TableSessionService;
import com.ttip.mesa_agil.dto.requests.CreateTableSessionRequest;
import com.ttip.mesa_agil.dto.responses.TableSessionDetailsResponse;
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

    @PreAuthorize("hasRole('STAFF')")
    @PostMapping("/table/{tableId}")
    public ResponseEntity<TableSessionDetailsResponse> create(
            @PathVariable Long tableId,
            @RequestBody @Valid CreateTableSessionRequest request
    ) {
        return ResponseEntity.ok(
                service.createSession(tableId, request)
        );
    }

    @PreAuthorize("hasRole('STAFF')")
    @PatchMapping("/table/{tableId}/close")
    public ResponseEntity<Void> close(
            @PathVariable Long tableId
    ) {
        service.closeSession(tableId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{sessionId}/customers")
    public ResponseEntity<TableSessionDetailsResponse> updateCustomers(
            @PathVariable Long sessionId,
            @RequestBody @Valid UpdateCustomerCountRequest request
    ) {

        return ResponseEntity.ok(
                service.updateCustomerCount(
                        sessionId,
                        request
                )
        );
    }
}
