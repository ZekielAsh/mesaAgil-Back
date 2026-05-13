package com.ttip.mesa_agil.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdateItemRequest extends BaseItemRequest {
    private Long id;

    public UpdateItemRequest(Long id, String name, String description, String imageUrl, BigDecimal price, Long categoryId) {
        super(name, description, imageUrl, price, categoryId);
        this.id = id;
    }
}