package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.responses.StatsDashboardResponse;
import com.ttip.mesa_agil.dto.responses.StatsSummaryResponse;
import com.ttip.mesa_agil.helper.DateRange;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import com.ttip.mesa_agil.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    private DateRange resolveRange(StatsPeriod period) {
        return DateRange.resolvePeriod(period);
    }

    private BigDecimal getItemRevenue(OrderItem item) {
        return item.getUnitPrice().multiply(
                BigDecimal.valueOf(item.getQuantity())
        );
    }

    public StatsDashboardResponse getDashboard(StatsPeriod period) {
        DateRange range = resolveRange(period);

        return new StatsDashboardResponse(
                getSummary(range),
                getRevenuePoint(period, range),
                getCategoryRevenue(range),
                getTopProducts(range),
                getTopRevenueProducts(range),
                getTableOrders(range),
                getTableRevenue(range)
        );
    }

    public StatsSummaryResponse getSummary(StatsPeriod period) {
        return getSummary(resolveRange(period));
    }

    private StatsSummaryResponse getSummary(DateRange range) {
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

    public List<RevenuePointDto> getRevenuePoint(StatsPeriod period) {
        return getRevenuePoint(period, resolveRange(period));
    }

    private List<RevenuePointDto> getRevenuePoint(StatsPeriod period, DateRange range) {
        List<OrderItem> items = statsRepository.getRevenueTimelineData(
                range.from(),
                range.to()
        );
        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");

        if (period == StatsPeriod.LAST_YEAR) {

            Map<YearMonth, BigDecimal> revenueByMonth =
                    items.stream()
                            .collect(Collectors.groupingBy(
                                    oi -> YearMonth.from(
                                            oi.getOrder()
                                                    .getCreatedAt()
                                                    .atZone(zone)
                                    ),
                                    Collectors.reducing(
                                            BigDecimal.ZERO,
                                            this::getItemRevenue,
                                            BigDecimal::add
                                    )
                            ));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

            YearMonth currentMonth = YearMonth.from(range.to().atZone(zone));
            YearMonth firstMonth =YearMonth.from(range.from().atZone(zone));

            List<RevenuePointDto> timeline = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                YearMonth month = firstMonth.plusMonths(i);

                timeline.add(
                        new RevenuePointDto(
                                month.format(formatter),
                                revenueByMonth.getOrDefault(
                                        month,
                                        BigDecimal.ZERO
                                )
                        )
                );
            }
            return timeline;
        }

        Map<LocalDate, BigDecimal> revenueByDay =
                items.stream()
                        .collect(Collectors.groupingBy(
                                oi -> oi.getOrder()
                                        .getCreatedAt()
                                        .atZone(zone)
                                        .toLocalDate(),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        this::getItemRevenue,
                                        BigDecimal::add
                                )
                        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return revenueByDay.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new RevenuePointDto(
                        entry.getKey().format(formatter),
                        entry.getValue()
                ))
                .toList();
    }

    public List<CategoryRevenueDto> getCategoryRevenue(StatsPeriod period) {
        return getCategoryRevenue(resolveRange(period));
    }

    private List<CategoryRevenueDto> getCategoryRevenue(DateRange range) {
        return statsRepository.getCategoryRevenue(
                range.from(),
                range.to()
        );
    }

    public List<TableOrdersDto> getTableOrders(StatsPeriod period) {
        return getTableOrders(resolveRange(period));
    }

    private List<TableOrdersDto> getTableOrders(DateRange range) {
        return statsRepository.getTableOrders(
                range.from(),
                range.to()
        );
    }

    public List<TableRevenueDto> getTableRevenue(StatsPeriod period) {
        return getTableRevenue(resolveRange(period));
    }

    private List<TableRevenueDto> getTableRevenue(DateRange range) {
        return statsRepository.getTableRevenue(
                range.from(),
                range.to()
        );
    }

    public List<TopItemDto> getTopProducts(StatsPeriod period) {
        return getTopProducts(resolveRange(period));
    }

    private List<TopItemDto> getTopProducts(DateRange range) {
        return statsRepository
                .getTopProducts(
                        range.from(),
                        range.to()
                )
                .stream()
                .limit(5)
                .toList();
    }

    public List<TopRevenueItemDto> getTopRevenueProducts(StatsPeriod period) {
        return getTopRevenueProducts(resolveRange(period));
    }

    private List<TopRevenueItemDto> getTopRevenueProducts(DateRange range) {
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
