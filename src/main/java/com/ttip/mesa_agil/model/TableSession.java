package com.ttip.mesa_agil.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_table_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    @Column(nullable = false)
    private Integer customerCount;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @PrePersist
    public void onCreate() {
        startedAt = LocalDateTime.now();
        active = true;
    }
}