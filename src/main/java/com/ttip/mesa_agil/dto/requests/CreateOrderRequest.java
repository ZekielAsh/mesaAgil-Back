package com.ttip.mesa_agil.dto.requests;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull
        Long tableId
) { }
