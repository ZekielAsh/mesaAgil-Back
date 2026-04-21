package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.CreateOrderRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.exception.TableAlreadyHasOpenOrderException;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.dto.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final RestaurantTableService restaurantTableService;
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository, RestaurantTableService restaurantTableService) {
        this.orderRepository = orderRepository;
        this.restaurantTableService = restaurantTableService;
    }

    public OrderResponse create(CreateOrderRequest createOrderRequest) {
        RestaurantTable restaurantTable = restaurantTableService.getTableById(createOrderRequest.tableId());

        if (orderRepository.existsByTableIdAndStatus(createOrderRequest.tableId(), OrderStatus.OPEN)) {
            throw new TableAlreadyHasOpenOrderException(createOrderRequest.tableId());
        }

        Order order = orderRepository.save(CreateOrderRequest.toOrder(restaurantTable));

        return OrderResponse.from(order);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id " + orderId + " doesn't exist")
        );

        return OrderResponse.from(order);
    }

    public void closeOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id " + orderId + " doesn't exist")
        );

        order.setStatus(OrderStatus.CLOSED);
        orderRepository.save(order);
    }
}
