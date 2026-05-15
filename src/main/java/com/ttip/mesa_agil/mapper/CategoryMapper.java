package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.CategoryResponse;
import com.ttip.mesa_agil.model.FoodCategory;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponse toResponse(FoodCategory category) {

        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
