package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.ItemResponse;
import com.ttip.mesa_agil.model.Item;

public class ItemMapper {

    public static ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getImageUrl(),
                item.getPrice()
        );
    }
}
