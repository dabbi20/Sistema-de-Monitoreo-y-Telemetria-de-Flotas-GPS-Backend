package com.simon.telemetrysystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMetricsResponse {

    private Long totalVehicles;

    private Long movingVehicles;

    private Long stoppedVehicles;

    private Long noSignalVehicles;
}