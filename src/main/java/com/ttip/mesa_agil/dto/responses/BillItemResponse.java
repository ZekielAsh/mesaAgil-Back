package com.ttip.mesa_agil.dto.responses;

import java.math.BigDecimal;

public record BillItemResponse(
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}