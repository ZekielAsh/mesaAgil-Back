package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.requests.CreateOrderItemRequest;
import com.ttip.mesa_agil.dto.requests.CreateOrderItemsRequest;
import com.ttip.mesa_agil.exception.*;
import com.ttip.mesa_agil.helper.TableAssignmentValidator;
import com.ttip.mesa_agil.mapper.OrderMapper;
import com.ttip.mesa_agil.model.*;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import com.ttip.mesa_agil.repository.OrderRepository;
import com.ttip.mesa_agil.dto.responses.OrderResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuService menuService;
    private final TableSessionService tableSessionService;
    private final TableAssignmentValidator tableAssignmentValidator;

    public OrderService(OrderRepository orderRepository, MenuService menuService, TableSessionService tableSessionService, TableAssignmentValidator tableAssignmentValidator) {
        this.orderRepository = orderRepository;
        this.menuService = menuService;
        this.tableSessionService = tableSessionService;
        this.tableAssignmentValidator = tableAssignmentValidator;
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
                () -> new OrderNotFoundException(orderId));

        tableAssignmentValidator.validateCurrentUserAssigned(
                order.getTable().getId());

        if (order.getStatus() != OrderStatus.OPEN) { throw new OrderClosedException(orderId); }

        if (!order.isBillRequested()) { throw new OrderBillRequestException("The bill was not requested");}

        if (order.getItems().isEmpty()) { throw new OrderBillRequestEmptyException("The order items cannot be empty"); }

        order.setBillRequested(false);

        order.setStatus(OrderStatus.CLOSED);
        order.setClosedAt(Instant.now());

        tableSessionService.closeSession(
                order.getTable().getId()
        );
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse addItems(Long orderId, CreateOrderItemsRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.OPEN) { throw new OrderClosedException(orderId); }

        if (order.isBillRequested()) { throw new OrderBillRequestException("The order is on request bill"); }

        for (CreateOrderItemRequest req : request.orderItemRequestList()) {
            Item item = menuService.getItemById(req.itemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(req.quantity());
            orderItem.setUnitPrice(item.getPrice());
            orderItem.setStatus(OrderItemStatus.PENDING);

            order.getItems().add(orderItem);
        }

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse requestBill(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new OrderClosedException(orderId);
        }

        if(order.getItems().isEmpty()) {
            throw new OrderBillRequestEmptyException("The order items cannot be empty");
        }

        boolean hasPendingItems = order.getItems().stream()
                .anyMatch(item -> item.getStatus() != OrderItemStatus.DELIVERED);

        if (hasPendingItems) {
            throw new OrderHasUndeliveredItemsException("There are items that have not been delivered yet");
        }

        order.setBillRequested(true);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getBillRequestsForCurrentStaff() {
        User currentUser = tableAssignmentValidator.getCurrentUser();

        return orderRepository
                .findAllByBillRequestedTrueAndTable_AssignedStaff_Id(
                        currentUser.getId()
                )
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public Order getOpenOrderBySession(Long sessionId, OrderStatus status) {
        return orderRepository.findByTableSessionIdAndStatus(sessionId, status)
                .orElseThrow(() -> new BusinessException("No order found for session id: " + sessionId + " and status: " + status));
    }
}