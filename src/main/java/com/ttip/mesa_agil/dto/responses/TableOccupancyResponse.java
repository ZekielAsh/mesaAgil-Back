package com.ttip.mesa_agil.dto.responses;

import com.ttip.mesa_agil.model.enums.TableStatus;

public record TableOccupancyResponse(
        Long tableId,
        Integer tableNumber,
        TableStatus status,
        Integer customerCount,
        Long sessionId,
        Long assignedStaffId,
        String assignedStaffUsername
) {
}