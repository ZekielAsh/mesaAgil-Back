package com.ttip.mesa_agil.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseItemRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private Long categoryId;
}