package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.CategoryRevenueDto;
import com.ttip.mesa_agil.dto.RevenuePointDto;
import com.ttip.mesa_agil.dto.TableOrdersDto;
import com.ttip.mesa_agil.dto.TableRevenueDto;
import com.ttip.mesa_agil.dto.responses.StatsSummaryResponse;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import com.ttip.mesa_agil.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/summary")
    public ResponseEntity<StatsSummaryResponse> getSummary(
            @RequestParam StatsPeriod period
    ) {
        return ResponseEntity.ok(
                statsService.getSummary(period)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/revenue-timeline")
    public ResponseEntity<List<RevenuePointDto>>
    getRevenuePoint(
            @RequestParam StatsPeriod period
    ) {

        return ResponseEntity.ok(
                statsService.getRevenuePoint(period)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryRevenueDto>> getCategoryRevenue(
            @RequestParam StatsPeriod period
    ) {
        return ResponseEntity.ok(
                statsService.getCategoryRevenue(period)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tables/orders")
    public ResponseEntity<List<TableOrdersDto>> getTableOrders(
            @RequestParam StatsPeriod period
    ) {
        return ResponseEntity.ok(
                statsService.getTableOrders(period)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/tables/revenue")
    public ResponseEntity<List<TableRevenueDto>> getTableRevenue(
            @RequestParam StatsPeriod period
    ) {
        return ResponseEntity.ok(
                statsService.getTableRevenue(period)
        );
    }
}