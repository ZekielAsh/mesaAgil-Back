package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.CreateOrderRequest;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.model.RestaurantTable;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.dto.OrderResponse;
import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository, RestaurantTableRepository restaurantTableRepository) {
        this.orderRepository = orderRepository;
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public OrderResponse create(CreateOrderRequest createOrderRequest) {
        RestaurantTable restaurantTable =
                restaurantTableRepository.findById(createOrderRequest.tableId()).orElseThrow(
                        () -> new ResourceNotFoundException("Restaurant table with id " + createOrderRequest.tableId() + " doesn't exist")
                );

        Order order = orderRepository.save(CreateOrderRequest.toOrder(restaurantTable));

        return OrderResponse.from(order);
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order with id " + orderId + " doesn't exist")
        );

        return OrderResponse.from(order);
    }
}
