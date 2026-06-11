package com.ttip.mesa_agil.dto;

import java.math.BigDecimal;

public record CategoryRevenueDto(
        String category,
        BigDecimal revenue
) {
}