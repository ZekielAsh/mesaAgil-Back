package com.ttip.mesa_agil.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull
        Long tableId
) { }
