package com.simon.telemetrysystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "gps_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false)
    private Instant timestamp;
}