package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateOrderItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderRequest;
import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.dto.websocket.WebSocketEvent;
import com.ttip.mesa_agil.exception.*;
import com.ttip.mesa_agil.mapper.OrderItemMapper;
import com.ttip.mesa_agil.mapper.OrderMapper;
import com.ttip.mesa_agil.model.Item;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestaurantTableService restaurantTableService;
    private final MenuService menuService;

    public OrderService(
            OrderRepository orderRepository,
            RestaurantTableService restaurantTableService,
            MenuService menuService) {
        this.orderRepository = orderRepository;
        this.restaurantTableService = restaurantTableService;
        this.menuService = menuService;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest createOrderRequest) {
        RestaurantTable restaurantTable = restaurantTableService.getTableById(createOrderRequest.tableId());

        if (!restaurantTable.isEnabled()) {
            throw new RestaurantTableClosedException(createOrderRequest.tableId());
        }

        if (orderRepository.existsByTableIdAndStatus(createOrderRequest.tableId(), OrderStatus.OPEN)) {
            throw new TableAlreadyHasOpenOrderException(createOrderRequest.tableId());
        }

        Order order = OrderMapper.toEntity(restaurantTable);
        order.setStatus(OrderStatus.OPEN);

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );

        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse closeOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new OrderNotFoundException(orderId)
        );

        if (order.getStatus() == OrderStatus.CLOSED) {
            throw new OrderClosedException(orderId);
        }

        if (!order.isBillRequested()) {
            throw new OrderBillRequestException(
                    "The bill was not requested"
            );
        }

        if (order.getItems().isEmpty()) {
            throw new OrderBillRequestEmptyException(
                    "The order items cannot be empty"
            );
        }

        order.setBillRequested(false);

        order.setStatus(OrderStatus.CLOSED);
        order.setClosedAt(LocalDateTime.now());

        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse addItems(Long orderId, CreateOrderItemsRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.CLOSED) {
            throw new OrderClosedException(orderId);
        }

        if (order.isBillRequested()) {
            throw new OrderBillRequestException(
                    "The order is on request bill"
            );
        }

        Map<Long, OrderItem> existingItems = order.getItems().stream()
                .collect(Collectors.toMap(oi -> oi.getItem().getId(), oi -> oi));

        for (CreateOrderItemRequest req : request.orderItemRequestList()) {

            Item item = menuService.getItemById(req.itemId());

            OrderItem existing = existingItems.get(req.itemId());

            if (existing != null) {
                existing.setQuantity(existing.getQuantity() + req.quantity());
            } else {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setItem(item);
                orderItem.setQuantity(req.quantity());
                orderItem.setUnitPrice(item.getPrice());
                orderItem.setStatus(OrderItemStatus.PENDING);

                order.getItems().add(orderItem);
            }
        }

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse requestBill(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if(order.getItems().isEmpty()) {
            throw new OrderBillRequestEmptyException(
                    "The order items cannot be empty"
            );
        }

        boolean hasPendingItems = order.getItems().stream()
                .anyMatch(item ->
                        item.getStatus() != OrderItemStatus.DELIVERED);

        if (hasPendingItems) {
            throw new OrderHasUndeliveredItemsException(
                    "There are items that have not been delivered yet"
            );
        }

        order.setBillRequested(true);
        return OrderMapper.toResponse(order);
    }

    public List<OrderResponse> getBillRequests() {
        return orderRepository.findByBillRequestedTrue()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }
}