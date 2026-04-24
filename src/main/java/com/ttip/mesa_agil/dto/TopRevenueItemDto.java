package com.ttip.mesa_agil.dto;

import java.math.BigDecimal;

public record TopRevenueItemDto(
        String name,
        BigDecimal totalRevenue
) { }
