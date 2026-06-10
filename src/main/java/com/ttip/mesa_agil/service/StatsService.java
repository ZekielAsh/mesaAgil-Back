package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.responses.StatsSummaryResponse;
import com.ttip.mesa_agil.helper.DateRange;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import com.ttip.mesa_agil.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public StatsSummaryResponse getSummary(StatsPeriod period) {

        DateRange range = DateRange.resolvePeriod(period);

        BigDecimal totalRevenue = statsRepository.getTotalRevenue(
                range.from(),
                range.to()
        );

        Long totalOrders = statsRepository.getTotalOrders(
                range.from(),
                range.to()
        );

        BigDecimal avgTicket = totalOrders == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(
                BigDecimal.valueOf(totalOrders),
                2,
                RoundingMode.HALF_UP
        );

        return new StatsSummaryResponse(
                totalRevenue,
                totalOrders,
                avgTicket
        );
    }

    public List<RevenuePointDto> getRevenuePoint(
            StatsPeriod period
    ) {

        DateRange range = DateRange.resolvePeriod(period);

        Map<LocalDate, BigDecimal> revenueByDay =
                statsRepository.getRevenueTimelineData(
                                range.from(),
                                range.to()
                        )
                        .stream()
                        .collect(Collectors.groupingBy(
                                oi -> oi.getOrder()
                                        .getCreatedAt()
                                        .toLocalDate(),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        oi -> oi.getUnitPrice().multiply(
                                                BigDecimal.valueOf(
                                                        oi.getQuantity()
                                                )
                                        ),
                                        BigDecimal::add
                                )
                        ));

        return revenueByDay.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new RevenuePointDto(
                        entry.getKey().format(
                                DateTimeFormatter.ofPattern("dd/MM")
                        ),
                        entry.getValue()
                ))
                .toList();
    }

    public List<CategoryRevenueDto> getCategoryRevenue(
            StatsPeriod period
    ) {

        DateRange range = DateRange.resolvePeriod(period);

        return statsRepository.getCategoryRevenue(
                range.from(),
                range.to()
        );
    }

    public List<TableOrdersDto> getTableOrders(
            StatsPeriod period
    ) {

        DateRange range = DateRange.resolvePeriod(period);

        return statsRepository.getTableOrders(
                range.from(),
                range.to()
        );
    }

    public List<TableRevenueDto> getTableRevenue(
            StatsPeriod period
    ) {

        DateRange range = DateRange.resolvePeriod(period);

        return statsRepository.getTableRevenue(
                range.from(),
                range.to()
        );
    }

    public List<TopItemDto> getTopProducts(
            StatsPeriod period
    ) {

        DateRange range =
                DateRange.resolvePeriod(period);

        return statsRepository
                .getTopProducts(
                        range.from(),
                        range.to()
                )
                .stream()
                .limit(5)
                .toList();
    }

    public List<TopRevenueItemDto> getTopRevenueProducts(
            StatsPeriod period
    ) {

        DateRange range =
                DateRange.resolvePeriod(period);

        return statsRepository
                .getTopRevenueProducts(
                        range.from(),
                        range.to()
                )
                .stream()
                .limit(5)
                .toList();
    }
}
