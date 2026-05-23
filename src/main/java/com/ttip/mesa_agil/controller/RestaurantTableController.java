package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.responses.RestaurantTableQrResponse;
import com.ttip.mesa_agil.dto.responses.TableSessionResponse;
import com.ttip.mesa_agil.service.RestaurantTableService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tables")
@RequiredArgsConstructor
@Validated
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{tableId}/qr")
    public ResponseEntity<byte[]> getQrImage(@PathVariable @Min(1) Long tableId,
                                             @RequestParam(defaultValue = "false") boolean download) {
        ContentDisposition contentDisposition = ContentDisposition
                .builder(download ? "attachment" : "inline")
                .filename("mesa-" + tableId + "-qr.png")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(restaurantTableService.getQrPng(tableId));
    }

    @GetMapping("/qr/{qrToken}/image")
    public ResponseEntity<byte[]> getPublicQrImage(@PathVariable String qrToken,
                                                   @RequestParam(defaultValue = "false") boolean download) {
        ContentDisposition contentDisposition = ContentDisposition
                .builder(download ? "attachment" : "inline")
                .filename("mesa-qr.png")
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(restaurantTableService.getQrPngByToken(qrToken));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{tableId}/qr-info")
    public ResponseEntity<RestaurantTableQrResponse> getQrInfo(@PathVariable @Min(1) Long tableId) {
        return ResponseEntity.ok(restaurantTableService.getQrInfo(tableId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/qr-info")
    public ResponseEntity<List<RestaurantTableQrResponse>> getAllQrInfo() {
        return ResponseEntity.ok(restaurantTableService.getAllQrInfo());
    }

    @GetMapping("/qr/{qrToken}/session")
    public ResponseEntity<TableSessionResponse> resolveSession(@PathVariable String qrToken) {
        return ResponseEntity.ok(restaurantTableService.resolveSession(qrToken));
    }

    @GetMapping("/qr/{qrToken}/redirect")
    public ResponseEntity<Void> redirectToSession(@PathVariable String qrToken) {
        URI sessionUri = restaurantTableService.buildFrontendSessionUri(qrToken);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(sessionUri)
                .build();
    }
}
