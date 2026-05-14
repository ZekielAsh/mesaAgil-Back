package com.ttip.mesa_agil.dto.responses;

import java.math.BigDecimal;

public record ItemResponse(Long id,
                           String name,
                           String description,
                           String imageUrl,
                           BigDecimal price,
                           String categoryName)
{
}
