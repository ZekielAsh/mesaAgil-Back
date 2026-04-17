package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public RestaurantTable create(CreateRestaurantTableRequest createRestaurantTableRequest) {
        return restaurantTableRepository.save(
                CreateRestaurantTableRequest.toRestaurantTable(createRestaurantTableRequest)
        );
    }
}
