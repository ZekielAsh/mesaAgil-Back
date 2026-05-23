package com.ttip.mesa_agil.dto.responses;

public record RestaurantTableQrResponse(
        Long tableId,
        int tableNumber,
        String qrToken,
        String scanUrl,
        String qrImageUrl
) { }
