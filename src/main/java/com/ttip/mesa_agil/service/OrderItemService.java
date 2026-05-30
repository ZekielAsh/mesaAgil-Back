package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.mapper.OrderItemMapper;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public List<OrderItemResponse> getKitchenOrderItems() {
        List<OrderItem> orderItemsList = orderItemRepository.findAllByStatusInOrderByCreatedAtAsc(
                List.of(
                        OrderItemStatus.PENDING,
                        OrderItemStatus.IN_PREPARATION
                )
        );

        return OrderItemMapper.toResponseList(orderItemsList);
    }

    @Transactional
    public OrderItemResponse updateOrderItemStatus(
            Long orderItemId,
            OrderItemStatus status
    ) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));

        orderItem.setStatus(status);

        return OrderItemMapper.toResponse(orderItemRepository.save(orderItem));
    }
}
