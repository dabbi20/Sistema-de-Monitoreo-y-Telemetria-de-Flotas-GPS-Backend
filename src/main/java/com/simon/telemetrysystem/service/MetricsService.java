package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.dto.VehicleMetricsResponse;
import com.simon.telemetrysystem.model.Vehicle;
import com.simon.telemetrysystem.model.VehicleStatus;
import com.simon.telemetrysystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsService {

    private final VehicleRepository vehicleRepository;
    private final VehicleStatusService vehicleStatusService;

    public VehicleMetricsResponse getMetrics() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        long movingVehicles = 0;
        long stoppedVehicles = 0;
        long noSignalVehicles = 0;

        for (Vehicle vehicle : vehicles) {
            VehicleStatus currentStatus = vehicleStatusService.calculateStatus(
                    vehicle,
                    Instant.now()
            );

            vehicle.setStatus(currentStatus);
            vehicleRepository.save(vehicle);

            if (currentStatus == VehicleStatus.EN_MOVIMIENTO) {
                movingVehicles++;
            } else if (currentStatus == VehicleStatus.DETENIDO) {
                stoppedVehicles++;
            } else if (currentStatus == VehicleStatus.SIN_SENAL) {
                noSignalVehicles++;
            }
        }

        return VehicleMetricsResponse.builder()
                .totalVehicles((long) vehicles.size())
                .movingVehicles(movingVehicles)
                .stoppedVehicles(stoppedVehicles)
                .noSignalVehicles(noSignalVehicles)
                .build();
    }
}