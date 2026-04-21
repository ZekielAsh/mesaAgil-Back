package com.ttip.mesa_agil.dto;

import com.ttip.mesa_agil.model.RestaurantTable;
import jakarta.validation.constraints.NotNull;

public record CreateRestaurantTableRequest(
        @NotNull
        int number
) {
    public static RestaurantTable toRestaurantTable(CreateRestaurantTableRequest createRestaurantTableRequest) {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setNumber(createRestaurantTableRequest.number());

        return restaurantTable;
    }
}
