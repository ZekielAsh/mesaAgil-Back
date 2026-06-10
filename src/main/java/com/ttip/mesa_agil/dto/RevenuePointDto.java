package com.ttip.mesa_agil.dto;

import java.math.BigDecimal;

public record RevenuePointDto(
        String label,
        BigDecimal revenue
) {
}
