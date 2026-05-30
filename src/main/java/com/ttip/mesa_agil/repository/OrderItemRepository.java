package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.OrderItem;
import com.ttip.mesa_agil.model.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByStatusInOrderByCreatedAtAsc(
            List<OrderItemStatus> statuses
    );
}
