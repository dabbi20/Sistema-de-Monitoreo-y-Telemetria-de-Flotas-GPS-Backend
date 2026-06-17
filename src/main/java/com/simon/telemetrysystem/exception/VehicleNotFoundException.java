package com.simon.telemetrysystem.exception;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String vehicleId) {
        super("Vehículo no encontrado: " + vehicleId);
    }
}