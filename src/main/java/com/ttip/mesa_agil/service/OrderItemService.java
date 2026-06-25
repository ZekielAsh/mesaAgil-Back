package com.ttip.mesa_agil.service;

import com.ttip.mesa_agil.dto.responses.OrderItemResponse;
import com.ttip.mesa_agil.exception.ResourceNotFoundException;
import com.ttip.mesa_agil.helper.TableAssignmentValidator;
import com.ttip.mesa_agil.mapper.OrderItemMapper;
import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.User;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import com.ttip.mesa_agil.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final TableAssignmentValidator tableAssignmentValidator;

    public OrderItemService(OrderItemRepository orderItemRepository, TableAssignmentValidator tableAssignmentValidator) {
        this.orderItemRepository = orderItemRepository;
        this.tableAssignmentValidator = tableAssignmentValidator;
    }

    public List<OrderItemResponse> getOrderItemsByStatusList(List<OrderItemStatus> statusList) {
        List<OrderItem> orderItemsList = orderItemRepository.findAllByStatusInOrderByCreatedAtAsc(
                statusList
        );

        return OrderItemMapper.toResponseList(orderItemsList);
    }

    public List<OrderItemResponse> getReadyOrderItemsForCurrentStaff() {

        User currentUser = tableAssignmentValidator.getCurrentUser();

        return orderItemRepository
                .findAllByStatusAndOrder_Table_AssignedStaff_Id(
                        OrderItemStatus.READY,
                        currentUser.getId()
                )
                .stream()
                .map(OrderItemMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderItemResponse updateOrderItemStatus(Long orderItemId, OrderItemStatus status) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found"));

        if (status == OrderItemStatus.DELIVERED) {
            tableAssignmentValidator.validateCurrentUserAssigned(
                    orderItem.getOrder()
                            .getTable()
                            .getId()
            );
        }

        orderItem.setStatus(status);

        return OrderItemMapper.toResponse(orderItemRepository.save(orderItem));
    }
}
