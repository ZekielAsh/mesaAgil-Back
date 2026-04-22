package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.mapper.RestaurantTableMapper;
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
        return restaurantTableRepository.findById(tableId).orElseThrow(
                () -> new ResourceNotFoundException(tableId)
        );
    }

    public RestaurantTable create(CreateRestaurantTableRequest createRestaurantTableRequest) {
        return restaurantTableRepository.save(
                RestaurantTableMapper.toEntity(createRestaurantTableRequest)
        );
    }
}
