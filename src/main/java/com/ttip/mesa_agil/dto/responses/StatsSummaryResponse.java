package com.ttip.mesa_agil.dto.responses;

import java.math.BigDecimal;

public record StatsSummaryResponse(

        BigDecimal totalRevenue,
        Long totalOrders,
        BigDecimal avgTicket,

        String topItemName,
        Long topItemQuantity,

        String topRevenueItemName,
        BigDecimal topRevenueItemAmount
) { }
