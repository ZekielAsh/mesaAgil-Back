package com.ttip.mesa_agil.dto.requests;

import jakarta.validation.constraints.Min;

public record CreateTableSessionRequest(
        @Min(0)
        Integer customerCount
) {
}
