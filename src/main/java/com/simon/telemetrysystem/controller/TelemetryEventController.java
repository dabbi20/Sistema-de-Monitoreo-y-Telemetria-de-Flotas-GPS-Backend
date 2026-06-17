package com.simon.telemetrysystem.controller;

import com.simon.telemetrysystem.dto.TelemetryEventResponse;
import com.simon.telemetrysystem.service.TelemetryEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TelemetryEventController {

    private final TelemetryEventService telemetryEventService;

    @GetMapping("/events")
    public List<TelemetryEventResponse> getAllEvents() {
        return telemetryEventService.getAllEvents();
    }

    @GetMapping("/vehicles/{vehicleId}/events")
    public List<TelemetryEventResponse> getVehicleEvents(
            @PathVariable String vehicleId
    ) {
        return telemetryEventService.getEventsByVehicle(vehicleId);
    }
}