package com.ttip.mesa_agil.dto.requests;

import jakarta.validation.constraints.Min;

public record CreateTableSessionRequest(
        @Min(1)
        Integer customerCount
) {
}
