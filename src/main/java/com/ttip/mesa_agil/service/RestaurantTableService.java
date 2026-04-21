package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }
    // TODO: Change to dto once we need to create tables.
    public RestaurantTable getTableById(Long tableId) {
        RestaurantTable restaurantTable =
                restaurantTableRepository.findById(tableId).orElseThrow(
                        () -> new ResourceNotFoundException("Restaurant table with id " + tableId + " doesn't exist")
                );

        return restaurantTable;
    }

    public RestaurantTable create(CreateRestaurantTableRequest createRestaurantTableRequest) {
        return restaurantTableRepository.save(
                CreateRestaurantTableRequest.toRestaurantTable(createRestaurantTableRequest)
        );
    }
}
