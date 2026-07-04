package com.ttip.mesa_agil.helper;

import com.ttip.mesa_agil.model.enums.StatsPeriod;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record DateRange(Instant from, Instant to) {

    public static DateRange resolvePeriod(StatsPeriod period) {

        Instant now = Instant.now();

        Instant from = switch (period) {
            case LAST_DAY -> now.minus(1, ChronoUnit.DAYS);
            case LAST_WEEK -> now.minus(7, ChronoUnit.DAYS);
            case LAST_MONTH -> now.minus(30, ChronoUnit.DAYS);
            case LAST_YEAR -> now.minus(365, ChronoUnit.DAYS);
        };

        return new DateRange(from, now);
    }
}