package com.simon.telemetrysystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "telemetry_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private VehicleStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private VehicleStatus newStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private TelemetryEventType eventType;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(nullable = false)
    private String description;
}