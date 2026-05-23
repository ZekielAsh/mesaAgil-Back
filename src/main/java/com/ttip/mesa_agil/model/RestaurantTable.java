package com.ttip.mesa_agil.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "t_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Column(name = "qr_token", unique = true, length = 36)
    private String qrToken;

    @PrePersist
    public void ensureQrToken() {
        if (qrToken == null || qrToken.isBlank()) {
            qrToken = UUID.randomUUID().toString();
        }
    }
}
