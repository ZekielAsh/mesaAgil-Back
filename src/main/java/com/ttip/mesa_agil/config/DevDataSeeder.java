package com.ttip.mesa_agil.config;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderRequest;
import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.model.*;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.service.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class DevDataSeeder implements CommandLineRunner {

    private final OrderService orderService;
    private final RestaurantTableService restaurantTableService;
    private final MenuService menuService;
    private final FoodCategoryService foodCategoryService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    public DevDataSeeder(
            OrderService orderService,
            RestaurantTableService restaurantTableService,
            MenuService menuService,
            FoodCategoryService foodCategoryService,
            UserService userService,
            OrderRepository orderRepository) {
        this.orderService = orderService;
        this.restaurantTableService = restaurantTableService;
        this.menuService = menuService;
        this.foodCategoryService = foodCategoryService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {

        if (menuService.isEmpty()) {
            seedMenu();
        }

        seedUsers();

        /*--------------*/
        List<RestaurantTable> restaurantTableList = IntStream.rangeClosed(1, 10)
                .mapToObj(number ->
                        restaurantTableService.create(
                                new CreateRestaurantTableRequest(number)
                        )
                )
                .toList();
        /*--------------*/
        List<MenuItemDTO> menu = menuService.getMenu().getItems();

        Random random = new Random();
        /*--------------*/
        restaurantTableList.forEach(table -> {
            for (int i = 0; i < 3; i++) {
                createHistoricalOrder(
                        new CreateOrderRequest(table.getId()),
                        menu,
                        random,
                        LocalDateTime.now().minusDays(30)
                );
            }
        });
        /*--------------*/
    }

    private void createHistoricalOrder(
            CreateOrderRequest createOrderRequest,
            List<MenuItemDTO> menu,
            Random random,
            LocalDateTime date) {
        Order actualOrder = createOrderWithDate(createOrderRequest.tableId(), date);

        List<CreateOrderItemRequest> randomItems = createRandomOrderItems(menu, random);
        addItemsWithDate(actualOrder, new CreateOrderItemsRequest(randomItems), date);

        actualOrder.setBillRequested(false);
        actualOrder.setStatus(OrderStatus.CLOSED);
        actualOrder.setClosedAt(LocalDateTime.now().minusDays(30).plusHours(1));

        orderRepository.save(actualOrder);
    }

    private List<CreateOrderItemRequest> createRandomOrderItems(List<MenuItemDTO> menu, Random random) {
        List<CreateOrderItemRequest> orderItemRequestList = new ArrayList<>();

        for (MenuItemDTO menuItemDTO : menu) {
            orderItemRequestList.add(new CreateOrderItemRequest(
                    menuItemDTO.getId(),
                    1 + random.nextInt(10)
            ));
        }

        return orderItemRequestList;
    }

    private Order createOrderWithDate(Long tableId, LocalDateTime date) {
        RestaurantTable table = restaurantTableService.getTableById(tableId);

        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.CLOSED);
        order.setCreatedAt(date);
        order.setBillRequested(true);

        return orderRepository.save(order);
    }

    private void addItemsWithDate(Order order, CreateOrderItemsRequest request, LocalDateTime date) {
        for (CreateOrderItemRequest req : request.orderItemRequestList()) {
            Item item = menuService.getItemById(req.itemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(req.quantity());
            orderItem.setUnitPrice(item.getPrice());
            orderItem.setStatus(OrderItemStatus.DELIVERED);
            orderItem.setCreatedAt(date);

            order.getItems().add(orderItem);
        }

        orderRepository.save(order);
    }

    private void seedMenu() {
        // Seeds the menu with categories and items.

        FoodCategory food = foodCategoryService.create("Comidas");
        FoodCategory drinks = foodCategoryService.create("Bebidas");
        FoodCategory dessert = foodCategoryService.create("Postres");

        menuService.createItem(
                "Hamburguesa",
                "Carne con queso y pan",
                "https://static01.nyt.com/images/2025/07/25/multimedia/kla-diner-style-burger-fkmj/kla-diner-style-burger-fkmj-mediumSquareAt3X.jpg",
                new BigDecimal("2500.0"),
                food.getId()
        );

        menuService.createItem(
                "Pizza",
                "Pizza muzzarella clásica",
                "https://www.hunts.com/sites/g/files/qyyrlu211/files/uploadedImages/img_6934_48664.jpg",
                new BigDecimal("3000.0"),
                food.getId()
        );

        menuService.createItem(
                "Papas fritas",
                "Papas crocantes",
                "https://cocina-casera.com/wp-content/uploads/2023/01/patatas-fritas-crujientes-francesa-1-770x485.jpg",
                new BigDecimal("1500.0"),
                food.getId()
        );

        menuService.createItem(
                "Coca Cola",
                "500ml",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRrVsdrTQX4Ge83BprSrK5R0vWD2Kmxr8JDGw&s",
                new BigDecimal("1200.0"),
                drinks.getId()
        );

        menuService.createItem(
                "Flan",
                "Flan casero con dulce de leche",
                "https://www.divinacocina.es/wp-content/uploads/flan-de-dulce-de-leche.plato_.jpg",
                new BigDecimal("1800.0"),
                dessert.getId()
        );
    }

    private void seedUsers() {
        userService.createAdmin("admin", "admin123");
        userService.createKitchen("kitchen", "kitchen123");
        userService.createStaff("staff", "staff123");
    }
}
