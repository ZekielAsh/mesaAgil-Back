package com.ttip.mesa_agil.dto.responses;

import com.ttip.mesa_agil.dto.CategoryRevenueDto;
import com.ttip.mesa_agil.dto.RevenuePointDto;
import com.ttip.mesa_agil.dto.TableOrdersDto;
import com.ttip.mesa_agil.dto.TableRevenueDto;
import com.ttip.mesa_agil.dto.TopItemDto;
import com.ttip.mesa_agil.dto.TopRevenueItemDto;

import java.util.List;

public record StatsDashboardResponse(
        StatsSummaryResponse summary,
        List<RevenuePointDto> revenueTimeline,
        List<CategoryRevenueDto> categoryRevenue,
        List<TopItemDto> topProducts,
        List<TopRevenueItemDto> topRevenueProducts,
        List<TableOrdersDto> tableOrders,
        List<TableRevenueDto> tableRevenue
) {
}
