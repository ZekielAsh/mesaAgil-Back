package com.ttip.mesa_agil.config;

import com.ttip.mesa_agil.dto.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.CreateOrderRequest;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.service.MenuService;
import com.ttip.mesa_agil.service.OrderService;
import com.ttip.mesa_agil.service.RestaurantTableService;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;

@Component
public class DevDataSeeder implements CommandLineRunner {

    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;
    private final MenuService menuService;

    public DevDataSeeder(OrderService orderService, RestaurantTableService restaurantTableService, MenuService menuService) {
        this.orderService = orderService;
        this.restaurantTableService = restaurantTableService;
        this.menuService = menuService;
    }

    @Override
    public void run(String... args) {

        if (menuService.isEmpty()) {

            menuService.createItem(
                    "Hamburguesa",
                    "Carne con queso y pan",
                    "https://static01.nyt.com/images/2025/07/25/multimedia/kla-diner-style-burger-fkmj/kla-diner-style-burger-fkmj-mediumSquareAt3X.jpg",
                    new BigDecimal("2500.0")
            );

            menuService.createItem(
                    "Pizza",
                    "Pizza muzzarella clásica",
                    "https://www.hunts.com/sites/g/files/qyyrlu211/files/uploadedImages/img_6934_48664.jpg",
                    new BigDecimal("3000.0")
            );

            menuService.createItem(
                    "Papas fritas",
                    "Papas crocantes",
                    "https://cocina-casera.com/wp-content/uploads/2023/01/patatas-fritas-crujientes-francesa-1-770x485.jpg",
                    new BigDecimal("1500.0")
            );

            System.out.println("Items cargados");
        }


        CreateRestaurantTableRequest createRestaurantTableRequest = new CreateRestaurantTableRequest(1);
        RestaurantTable restaurantTable = restaurantTableService.create(createRestaurantTableRequest);

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(restaurantTable.getId());

        orderService.create(createOrderRequest);
    }
}
