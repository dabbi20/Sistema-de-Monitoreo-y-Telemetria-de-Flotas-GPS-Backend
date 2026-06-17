package com.simon.telemetrysystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simon.telemetrysystem.model.VehicleStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("last_lat")
    private Double lastLat;

    @JsonProperty("last_lng")
    private Double lastLng;

    @JsonProperty("last_seen")
    private Instant lastSeen;

    private VehicleStatus status;
}