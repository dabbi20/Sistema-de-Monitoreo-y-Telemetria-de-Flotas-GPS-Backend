package com.simon.telemetrysystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private String vehicleId;

    @Column(name = "last_lat")
    private Double lastLat;

    @Column(name = "last_lng")
    private Double lastLng;

    @Column(name = "previous_lat")
    private Double previousLat;

    @Column(name = "previous_lng")
    private Double previousLng;

    @Column(name = "last_seen")
    private Instant lastSeen;

    @Column(name = "last_position_changed_at")
    private Instant lastPositionChangedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;
}