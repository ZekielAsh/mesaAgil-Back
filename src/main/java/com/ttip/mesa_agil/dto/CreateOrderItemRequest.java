package com.ttip.mesa_agil.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
        @NotNull
        Long itemId,

        @Min(1)
        int quantity
) { }
