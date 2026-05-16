package com.ttip.mesa_agil.dto.requests;

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
        super(name, description, imageUrl, price, categoryId, true);
        this.id = id;
    }

    public UpdateItemRequest(Long id, String name, String description, String imageUrl, BigDecimal price, Long categoryId, Boolean active) {
        super(name, description, imageUrl, price, categoryId, active);
        this.id = id;
    }
}
