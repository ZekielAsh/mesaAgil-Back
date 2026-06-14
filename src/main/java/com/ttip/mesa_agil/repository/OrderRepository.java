package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.Order;
import com.ttip.mesa_agil.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByTableIdAndStatus(Long tableId, OrderStatus status);

    Optional<Order> findFirstByTableIdAndStatusOrderByCreatedAtDesc(Long tableId, OrderStatus status);

    List<Order> findByBillRequestedTrue();

    Optional<Order> findByTableSessionId(Long sessionId);

    Optional<Order> findByTableSessionIdAndStatus(
            Long sessionId,
            OrderStatus status
    );

    boolean existsByTableSessionIdAndStatus(
            Long sessionId,
            OrderStatus status
    );
}
