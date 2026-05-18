package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.dto.TopItemDto;
import com.ttip.mesa_agil.dto.TopRevenueItemDto;
import com.ttip.mesa_agil.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// TODO: see how to change this to extend to a base class
public interface StatsRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT COALESCE(SUM(oi.unitPrice * oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
    """)
    BigDecimal getTotalRevenue(LocalDateTime from, LocalDateTime to);


    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.status = 'CLOSED'
        AND o.createdAt BETWEEN :from AND :to
    """)
    Long getTotalOrders(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT new com.ttip.mesa_agil.dto.TopItemDto(
            oi.item.name,
            SUM(oi.quantity)
        )
        FROM OrderItem oi
        WHERE oi.order.status = 'CLOSED'
        AND oi.order.createdAt BETWEEN :from AND :to
        GROUP BY oi.item.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<TopItemDto> getTopProducts(LocalDateTime from, LocalDateTime to);

    @Query("""
    SELECT new com.ttip.mesa_agil.dto.TopRevenueItemDto(
        oi.item.name,
        CAST(SUM(oi.unitPrice * oi.quantity) AS BIGDECIMAL)
    )
    FROM OrderItem oi
    WHERE oi.order.status = 'CLOSED'
    AND oi.order.createdAt BETWEEN :from AND :to
    GROUP BY oi.item.name
    ORDER BY SUM(oi.unitPrice * oi.quantity) DESC
""")
    List<TopRevenueItemDto> getTopRevenueProducts(LocalDateTime from, LocalDateTime to);
}
