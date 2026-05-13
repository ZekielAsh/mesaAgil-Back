package com.ttip.mesa_agil.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateItemRequest extends BaseItemRequest {
    public CreateItemRequest(String name, String description, String imageUrl, BigDecimal price, Long categoryId) {
        super(name, description, imageUrl, price, categoryId);
    }
}