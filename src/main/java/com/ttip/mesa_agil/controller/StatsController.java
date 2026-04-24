package com.ttip.mesa_agil.controller;

import com.ttip.mesa_agil.dto.StatsSummaryResponse;
import com.ttip.mesa_agil.model.enums.StatsPeriod;
import com.ttip.mesa_agil.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<StatsSummaryResponse> getSummary(
            @RequestParam StatsPeriod period
    ) {
        return ResponseEntity.ok(statsService.getSummary(period));
    }
}
