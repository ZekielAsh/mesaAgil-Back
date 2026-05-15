package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.model.RestaurantTable;

public class RestaurantTableMapper {

    public static RestaurantTable toEntity(CreateRestaurantTableRequest createRestaurantTableRequest) {
        RestaurantTable restaurantTable = new RestaurantTable();
        restaurantTable.setNumber(createRestaurantTableRequest.number());

        return restaurantTable;
    }
}
