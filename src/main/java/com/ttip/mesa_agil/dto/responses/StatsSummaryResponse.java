package com.ttip.mesa_agil.dto.responses;

import java.math.BigDecimal;

public record StatsSummaryResponse(

        BigDecimal totalRevenue,
        Long totalOrders,
        BigDecimal avgTicket
) { }
