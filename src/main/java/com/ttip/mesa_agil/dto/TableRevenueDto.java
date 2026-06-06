package com.ttip.mesa_agil.dto;

import java.math.BigDecimal;

public record TableRevenueDto(
        Integer tableNumber,
        BigDecimal revenue
) {
}