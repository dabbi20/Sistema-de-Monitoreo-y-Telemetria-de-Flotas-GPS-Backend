package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.model.Vehicle;
import com.simon.telemetrysystem.model.VehicleStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class VehicleStatusService {

    private static final long STOPPED_THRESHOLD_SECONDS = 60;
    private static final long NO_SIGNAL_THRESHOLD_SECONDS = 120;

    public VehicleStatus calculateStatus(Vehicle vehicle, Instant currentTimestamp) {

        if (vehicle == null) {
            throw new IllegalArgumentException("El vehículo no puede ser nulo");
        }

        if (currentTimestamp == null) {
            throw new IllegalArgumentException("El timestamp actual no puede ser nulo");
        }

        if (vehicle.getLastSeen() == null) {
            throw new IllegalStateException(
                    "El vehículo no tiene información de última comunicación"
            );
        }

        long secondsSinceLastSeen = Duration.between(
                vehicle.getLastSeen(),
                currentTimestamp
        ).getSeconds();

        if (secondsSinceLastSeen > NO_SIGNAL_THRESHOLD_SECONDS) {
            return VehicleStatus.SIN_SENAL;
        }

        if (hasPositionChanged(vehicle)) {
            return VehicleStatus.EN_MOVIMIENTO;
        }

        if (vehicle.getLastPositionChangedAt() != null) {
            long secondsWithoutPositionChange = Duration.between(
                    vehicle.getLastPositionChangedAt(),
                    currentTimestamp
            ).getSeconds();

            if (secondsWithoutPositionChange > STOPPED_THRESHOLD_SECONDS) {
                return VehicleStatus.DETENIDO;
            }
        }

        return vehicle.getStatus() != null
                ? vehicle.getStatus()
                : VehicleStatus.DETENIDO;
    }

    private boolean hasPositionChanged(Vehicle vehicle) {

        if (vehicle.getPreviousLat() == null
                || vehicle.getPreviousLng() == null) {
            return false;
        }

        return !vehicle.getPreviousLat().equals(vehicle.getLastLat())
                || !vehicle.getPreviousLng().equals(vehicle.getLastLng());
    }
}