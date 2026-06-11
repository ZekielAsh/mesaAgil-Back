package com.ttip.mesa_agil.helper;

import com.ttip.mesa_agil.model.enums.StatsPeriod;

import java.time.LocalDateTime;

public record DateRange(LocalDateTime from, LocalDateTime to) {

    public static DateRange resolvePeriod(StatsPeriod period) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime from = switch (period) {
            case LAST_DAY -> now.minusDays(1);
            case LAST_WEEK -> now.minusWeeks(1);
            case LAST_MONTH -> now.minusMonths(1);
        };

        return new DateRange(from, now);
    }
}