package com.ttip.mesa_agil.dto;

import jakarta.validation.constraints.NotNull;

public record CreateRestaurantTableRequest(
        @NotNull
        int number
) { }
