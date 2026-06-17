package com.simon.telemetrysystem.mapper;

import com.simon.telemetrysystem.dto.VehicleResponse;
import com.simon.telemetrysystem.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponse.builder()
                .vehicleId(vehicle.getVehicleId())
                .lastLat(vehicle.getLastLat())
                .lastLng(vehicle.getLastLng())
                .lastSeen(vehicle.getLastSeen())
                .status(vehicle.getStatus())
                .build();
    }
}