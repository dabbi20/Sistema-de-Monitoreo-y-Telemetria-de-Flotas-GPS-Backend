package com.simon.telemetrysystem.mapper;

import com.simon.telemetrysystem.dto.TelemetryEventResponse;
import com.simon.telemetrysystem.model.TelemetryEvent;
import org.springframework.stereotype.Component;

@Component
public class TelemetryEventMapper {

    public TelemetryEventResponse toResponse(TelemetryEvent telemetryEvent) {
        if (telemetryEvent == null) {
            return null;
        }

        return TelemetryEventResponse.builder()
                .vehicleId(telemetryEvent.getVehicleId())
                .description(telemetryEvent.getDescription())
                .previousStatus(telemetryEvent.getPreviousStatus())
                .newStatus(telemetryEvent.getNewStatus())
                .eventType(telemetryEvent.getEventType())
                .eventTime(telemetryEvent.getEventTime())
                .build();
    }
}