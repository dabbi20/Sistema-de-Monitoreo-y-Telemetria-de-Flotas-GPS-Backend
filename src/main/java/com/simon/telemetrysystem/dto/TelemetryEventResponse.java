package com.simon.telemetrysystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simon.telemetrysystem.model.TelemetryEventType;
import com.simon.telemetrysystem.model.VehicleStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemetryEventResponse {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    private String description;

    @JsonProperty("previous_status")
    private VehicleStatus previousStatus;

    @JsonProperty("new_status")
    private VehicleStatus newStatus;

    @JsonProperty("event_type")
    private TelemetryEventType eventType;

    @JsonProperty("event_time")
    private Instant eventTime;
}