package com.simon.telemetrysystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsRecordResponse {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    private Double lat;

    private Double lng;

    private Instant timestamp;
}