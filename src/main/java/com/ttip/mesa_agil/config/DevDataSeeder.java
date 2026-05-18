package com.ttip.mesa_agil.config;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderRequest;
import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.model.FoodCategory;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.service.FoodCategoryService;
import com.ttip.mesa_agil.service.MenuService;
import com.ttip.mesa_agil.service.OrderService;
import com.ttip.mesa_agil.service.RestaurantTableService;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DevDataSeeder implements CommandLineRunner {

    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;
    private final MenuService menuService;
    private final FoodCategoryService foodCategoryService;

    public DevDataSeeder(OrderService orderService, RestaurantTableService restaurantTableService, MenuService menuService, FoodCategoryService foodCategoryService) {
        this.orderService = orderService;
        this.restaurantTableService = restaurantTableService;
        this.menuService = menuService;
        this.foodCategoryService = foodCategoryService;
    }

    @Override
    public void run(String... args) {

        if (menuService.isEmpty()) {

            FoodCategory comidas = foodCategoryService.create("Comidas");
            FoodCategory bebidas = foodCategoryService.create("Bebidas");
            FoodCategory postres = foodCategoryService.create("Postres");

            menuService.createItem(
                    "Hamburguesa",
                    "Carne con queso y pan",
                    "https://static01.nyt.com/images/2025/07/25/multimedia/kla-diner-style-burger-fkmj/kla-diner-style-burger-fkmj-mediumSquareAt3X.jpg",
                    new BigDecimal("2500.0"),
                    comidas.getId()
            );

            menuService.createItem(
                    "Pizza",
                    "Pizza muzzarella clásica",
                    "https://www.hunts.com/sites/g/files/qyyrlu211/files/uploadedImages/img_6934_48664.jpg",
                    new BigDecimal("3000.0"),
                    comidas.getId()
            );

            menuService.createItem(
                    "Papas fritas",
                    "Papas crocantes",
                    "https://cocina-casera.com/wp-content/uploads/2023/01/patatas-fritas-crujientes-francesa-1-770x485.jpg",
                    new BigDecimal("1500.0"),
                    comidas.getId()
            );

            menuService.createItem(
                    "Coca Cola",
                    "500ml",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRrVsdrTQX4Ge83BprSrK5R0vWD2Kmxr8JDGw&s",
                    new BigDecimal("1200.0"),
                    bebidas.getId()
            );

            menuService.createItem(
                    "Flan",
                    "Flan casero con dulce de leche",
                    "https://www.divinacocina.es/wp-content/uploads/flan-de-dulce-de-leche.plato_.jpg",
                    new BigDecimal("1800.0"),
                    postres.getId()
            );

            System.out.println("Items cargados");
        }


        CreateRestaurantTableRequest createRestaurantTableRequest = new CreateRestaurantTableRequest(1);
        RestaurantTable restaurantTable = restaurantTableService.create(createRestaurantTableRequest);

        List<MenuItemDTO> menu = menuService.getMenu().getItems();

        Random random = new Random();

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(restaurantTable.getId());

        for (int i = 0; i < 5; i++ ) {
            OrderResponse order = orderService.create(createOrderRequest);

            List<CreateOrderItemRequest> orderItemRequestList = new ArrayList<>();

            for (MenuItemDTO menuItemDTO : menu) {
                orderItemRequestList.add(new CreateOrderItemRequest(
                        menuItemDTO.getId(),
                        (1 + random.nextInt(10)))
                );
            }

            orderService.addItems(order.id(), new CreateOrderItemsRequest(orderItemRequestList));

            orderService.closeOrderById(order.id());
        }

        OrderResponse actualOrder = orderService.create(createOrderRequest);

        orderService.addItems(
                actualOrder.id(),
                new CreateOrderItemsRequest(
                        List.of(
                                new CreateOrderItemRequest(1L, 2),
                                new CreateOrderItemRequest(2L, 1),
                                new CreateOrderItemRequest(3L, 1)
                        )
                )
        );

        List<CreateOrderItemRequest> orderItemRequestList = new ArrayList<>();

        for (MenuItemDTO menuItemDTO : menu) {
            orderItemRequestList.add(new CreateOrderItemRequest(
                    menuItemDTO.getId(),
                    (1 + random.nextInt(10)))
            );
        }

        orderService.addItems(actualOrder.id(), new CreateOrderItemsRequest(orderItemRequestList));
    }
}
