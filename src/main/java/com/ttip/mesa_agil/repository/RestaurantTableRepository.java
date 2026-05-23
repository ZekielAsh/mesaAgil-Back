package com.ttip.mesa_agil.repository;

import com.ttip.mesa_agil.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    boolean existsByQrToken(String qrToken);

    Optional<RestaurantTable> findByQrToken(String qrToken);
}
