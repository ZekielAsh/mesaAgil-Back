package com.ttip.mesa_agil.config;

import com.ttip.mesa_agil.dto.*;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.requests.CreateRestaurantTableRequest;
import com.ttip.mesa_agil.model.*;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.model.enums.UserRole;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import com.ttip.mesa_agil.repository.TableSessionRepository;
import com.ttip.mesa_agil.service.*;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class DevDataSeeder implements CommandLineRunner {

    private final RestaurantTableService restaurantTableService;
    private final MenuService menuService;
    private final FoodCategoryService foodCategoryService;
    private final UserService userService;

    private final RestaurantTableRepository restaurantTableRepository;
    private final TableSessionRepository tableSessionRepository;
    private final OrderRepository orderRepository;

    public DevDataSeeder(
            RestaurantTableService restaurantTableService,
            MenuService menuService,
            FoodCategoryService foodCategoryService,
            UserService userService,
            RestaurantTableRepository restaurantTableRepository,
            TableSessionRepository tableSessionRepository,
            OrderRepository orderRepository
    ) {
        this.restaurantTableService = restaurantTableService;
        this.menuService = menuService;
        this.foodCategoryService = foodCategoryService;
        this.userService = userService;
        this.restaurantTableRepository = restaurantTableRepository;
        this.tableSessionRepository = tableSessionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) {

        if (menuService.isEmpty()) {
            seedMenu();
        }

        if (restaurantTableRepository.count() == 0) {
            seedTables();
        }

        seedUsersIfNeeded();

        if (orderRepository.count() == 0) {
            seedHistoricalData();
        }
    }

    private void seedTables() {

        IntStream.rangeClosed(1, 10)
                .forEach(number ->
                        restaurantTableService.create(
                                new CreateRestaurantTableRequest(number)
                        ));
    }

    private void seedUsersIfNeeded() {
        userService.createIfNotExists(
                "admin",
                "admin123",
                UserRole.ADMIN
        );

        userService.createIfNotExists(
                "kitchen",
                "kitchen123",
                UserRole.KITCHEN
        );

        userService.createIfNotExists(
                "staff",
                "staff123",
                UserRole.STAFF
        );

        userService.createIfNotExists(
                "staff2",
                "staff234",
                UserRole.STAFF
        );
    }

    private void seedHistoricalData() {

        List<RestaurantTable> tables =
                restaurantTableRepository.findAll();

        List<MenuItemDTO> menu =
                menuService.getMenu().getItems();

        Random random = new Random();

        for (RestaurantTable table : tables) {

            int ordersCount =
                    10 + random.nextInt(15);

            for (int i = 0; i < ordersCount; i++) {

                LocalDateTime orderDate =
                        LocalDateTime.now()
                                .minusDays(
                                        random.nextInt(90)
                                )
                                .minusHours(
                                        random.nextInt(24)
                                );

                createHistoricalSessionAndOrder(
                        table,
                        menu,
                        random,
                        orderDate
                );
            }
        }
    }

    private void createHistoricalSessionAndOrder(
            RestaurantTable table,
            List<MenuItemDTO> menu,
            Random random,
            LocalDateTime date
    ) {

        TableSession session =
                createHistoricalSession(
                        table,
                        date,
                        random
                );

        if (Boolean.TRUE.equals(session.getActive())) {
            throw new IllegalStateException(
                    "Historical session was persisted as active"
            );
        }

        Order order =
                createHistoricalOrder(
                        session,
                        date
                );

        List<CreateOrderItemRequest> items =
                createRandomOrderItems(
                        menu,
                        random
                );

        addItemsWithDate(
                order,
                new CreateOrderItemsRequest(items),
                date
        );

        orderRepository.save(order);
    }

    private TableSession createHistoricalSession(
            RestaurantTable table,
            LocalDateTime date,
            Random random
    ) {

        TableSession session =
                new TableSession();

        session.setTable(table);

        session.setCustomerCount(
                random.nextInt(6) + 1
        );

        session.setActive(false);

        session.setStartedAt(date);

        session.setEndedAt(
                date.plusMinutes(
                        30 + random.nextInt(120)
                )
        );

        return tableSessionRepository.save(session);
    }

    private Order createHistoricalOrder(
            TableSession session,
            LocalDateTime date
    ) {

        Order order = new Order();

        order.setTable(
                session.getTable()
        );

        order.setTableSession(
                session
        );

        order.setStatus(
                OrderStatus.CLOSED
        );

        order.setBillRequested(
                false
        );

        order.setCreatedAt(date);

        order.setClosedAt(
                session.getEndedAt()
        );

        return orderRepository.save(order);
    }

    private List<CreateOrderItemRequest> createRandomOrderItems(
            List<MenuItemDTO> menu,
            Random random
    ) {

        int itemsCount =
                1 + random.nextInt(
                        Math.min(
                                4,
                                menu.size()
                        )
                );

        List<MenuItemDTO> shuffled =
                new ArrayList<>(menu);

        Collections.shuffle(
                shuffled,
                random
        );

        return shuffled.stream()
                .limit(itemsCount)
                .map(item ->
                        new CreateOrderItemRequest(
                                item.getId(),
                                1 + random.nextInt(5)
                        )
                )
                .toList();
    }

    private void addItemsWithDate(
            Order order,
            CreateOrderItemsRequest request,
            LocalDateTime date
    ) {

        for (CreateOrderItemRequest req :
                request.orderItemRequestList()) {

            Item item =
                    menuService.getItemById(
                            req.itemId()
                    );

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(
                    req.quantity()
            );
            orderItem.setUnitPrice(
                    item.getPrice()
            );
            orderItem.setStatus(
                    OrderItemStatus.DELIVERED
            );
            orderItem.setCreatedAt(
                    date
            );

            order.getItems()
                    .add(orderItem);
        }
    }

    private void seedMenu() {

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
}
