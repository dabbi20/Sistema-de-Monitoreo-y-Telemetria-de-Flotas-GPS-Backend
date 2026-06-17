package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.dto.TelemetryEventResponse;
import com.simon.telemetrysystem.exception.VehicleNotFoundException;
import com.simon.telemetrysystem.mapper.TelemetryEventMapper;
import com.simon.telemetrysystem.model.TelemetryEvent;
import com.simon.telemetrysystem.model.TelemetryEventType;
import com.simon.telemetrysystem.model.VehicleStatus;
import com.simon.telemetrysystem.repository.TelemetryEventRepository;
import com.simon.telemetrysystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetryEventService {

    private final TelemetryEventRepository telemetryEventRepository;
    private final VehicleRepository vehicleRepository;
    private final TelemetryEventMapper telemetryEventMapper;

    public TelemetryEvent registerEvent(
            String vehicleId,
            TelemetryEventType eventType,
            String description,
            VehicleStatus previousStatus,
            VehicleStatus newStatus
    ) {
        TelemetryEvent event = TelemetryEvent.builder()
                .vehicleId(vehicleId)
                .eventType(eventType)
                .description(description)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .eventTime(Instant.now())
                .build();

        return telemetryEventRepository.save(event);
    }

    public List<TelemetryEventResponse> getAllEvents() {
        return telemetryEventRepository.findAllByOrderByEventTimeDesc()
                .stream()
                .map(telemetryEventMapper::toResponse)
                .toList();
    }

    public List<TelemetryEventResponse> getEventsByVehicle(String vehicleId) {
        if (!vehicleRepository.existsByVehicleId(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }

        return telemetryEventRepository.findByVehicleIdOrderByEventTimeDesc(vehicleId)
                .stream()
                .map(telemetryEventMapper::toResponse)
                .toList();
    }
}