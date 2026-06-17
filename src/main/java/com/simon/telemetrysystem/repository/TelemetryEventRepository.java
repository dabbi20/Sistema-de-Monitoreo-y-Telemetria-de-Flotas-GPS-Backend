package com.simon.telemetrysystem.repository;

import com.simon.telemetrysystem.model.TelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, Long> {

    List<TelemetryEvent> findByVehicleIdOrderByEventTimeDesc(String vehicleId);

    List<TelemetryEvent> findAllByOrderByEventTimeDesc();
}