package com.ttip.mesa_agil.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateRestaurantTableRequest(
    @NotNull
    @Positive
    Integer number
) {
}
