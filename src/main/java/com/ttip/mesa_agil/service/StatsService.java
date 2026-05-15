package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.responses.StatsSummaryResponse;
import com.ttip.mesa_agil.dto.TopItemDto;
import com.ttip.mesa_agil.dto.TopRevenueItemDto;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import com.ttip.mesa_agil.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public StatsSummaryResponse getSummary(StatsPeriod period) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = switch (period) {
            case LAST_DAY -> now.minusDays(1);
            case LAST_WEEK -> now.minusWeeks(1);
            case LAST_MONTH -> now.minusMonths(1);
        };

        BigDecimal totalRevenue = statsRepository.getTotalRevenue(from, now);
        Long totalOrders = statsRepository.getTotalOrders(from, now);

        BigDecimal avgTicket = totalOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP);

        TopItemDto topProduct = statsRepository
                .getTopProducts(from, now)
                .stream()
                .findFirst()
                .orElse(new TopItemDto("-", 0L));

        TopRevenueItemDto topRevenueProduct = statsRepository
                .getTopRevenueProducts(from, now)
                .stream()
                .findFirst()
                .orElse(new TopRevenueItemDto("-", BigDecimal.ZERO));

        // TODO: average order time and peak hour

        return new StatsSummaryResponse(
                totalRevenue,
                totalOrders,
                avgTicket,
                topProduct.name(),
                topProduct.total(),
                topRevenueProduct.name(),
                topRevenueProduct.totalRevenue()
        );
    }
}
