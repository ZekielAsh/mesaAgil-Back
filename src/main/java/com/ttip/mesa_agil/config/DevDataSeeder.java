package com.ttip.mesa_agil.config;

import com.ttip.mesa_agil.dto.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.CreateOrderRequest;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.service.OrderService;
import com.ttip.mesa_agil.service.RestaurantTableService;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;

    public DevDataSeeder(OrderService orderService, RestaurantTableService restaurantTableService) {
        this.orderService = orderService;
        this.restaurantTableService = restaurantTableService;
    }

    @Override
    public void run(String... args) {
        CreateRestaurantTableRequest createRestaurantTableRequest = new CreateRestaurantTableRequest(1);
        RestaurantTable restaurantTable = restaurantTableService.create(createRestaurantTableRequest);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(restaurantTable.getId());

        orderService.create(createOrderRequest);
    }
}
