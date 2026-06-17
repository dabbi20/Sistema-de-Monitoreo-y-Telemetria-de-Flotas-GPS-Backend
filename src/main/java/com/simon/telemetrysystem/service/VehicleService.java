package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.dto.GpsRequest;
import com.simon.telemetrysystem.dto.GpsRecordResponse;
import com.simon.telemetrysystem.dto.VehicleResponse;
import com.simon.telemetrysystem.exception.VehicleNotFoundException;
import com.simon.telemetrysystem.mapper.GpsRecordMapper;
import com.simon.telemetrysystem.mapper.VehicleMapper;
import com.simon.telemetrysystem.model.*;
import com.simon.telemetrysystem.repository.GpsRecordRepository;
import com.simon.telemetrysystem.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final GpsRecordRepository gpsRecordRepository;
    private final VehicleStatusService vehicleStatusService;
    private final TelemetryEventService telemetryEventService;
    private final VehicleMapper vehicleMapper;
    private final GpsRecordMapper gpsRecordMapper;

    public VehicleResponse processGpsData(GpsRequest gpsRequest) {

        Vehicle vehicle = vehicleRepository.findByVehicleId(gpsRequest.getVehicleId())
                .map(existingVehicle -> updateExistingVehicle(existingVehicle, gpsRequest))
                .orElseGet(() -> createNewVehicle(gpsRequest));

        saveGpsRecord(gpsRequest);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.toResponse(savedVehicle);
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicle -> {
                    VehicleStatus currentStatus = vehicleStatusService.calculateStatus(
                            vehicle,
                            java.time.Instant.now()
                    );

                    vehicle.setStatus(currentStatus);
                    Vehicle savedVehicle = vehicleRepository.save(vehicle);

                    return vehicleMapper.toResponse(savedVehicle);
                })
                .toList();
    }

    public VehicleResponse getVehicleById(String vehicleId) {
        Vehicle vehicle = getVehicleOrThrow(vehicleId);

        VehicleStatus currentStatus = vehicleStatusService.calculateStatus(
                vehicle,
                java.time.Instant.now()
        );

        vehicle.setStatus(currentStatus);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.toResponse(savedVehicle);
    }

    public void deleteVehicle(String vehicleId) {
        Vehicle vehicle = getVehicleOrThrow(vehicleId);

        telemetryEventService.registerEvent(
                vehicle.getVehicleId(),
                TelemetryEventType.VEHICLE_DELETED,
                "Vehículo eliminado del sistema: " + vehicle.getVehicleId(),
                vehicle.getStatus(),
                null
        );

        vehicleRepository.deleteByVehicleId(vehicleId);
    }

    public List<GpsRecordResponse> getVehicleRecords(String vehicleId) {
        if (!vehicleRepository.existsByVehicleId(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }

        return gpsRecordRepository.findByVehicleIdOrderByTimestampDesc(vehicleId)
                .stream()
                .map(gpsRecordMapper::toResponse)
                .toList();
    }

    private Vehicle createNewVehicle(GpsRequest gpsRequest) {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(gpsRequest.getVehicleId())
                .lastLat(gpsRequest.getLat())
                .lastLng(gpsRequest.getLng())
                .previousLat(null)
                .previousLng(null)
                .lastSeen(gpsRequest.getTimestamp())
                .lastPositionChangedAt(gpsRequest.getTimestamp())
                .status(VehicleStatus.DETENIDO)
                .build();

        telemetryEventService.registerEvent(
                vehicle.getVehicleId(),
                TelemetryEventType.VEHICLE_CREATED,
                "Vehículo creado con estado inicial DETENIDO: " + vehicle.getVehicleId(),
                null,
                VehicleStatus.DETENIDO
        );

        return vehicle;
    }

    private Vehicle updateExistingVehicle(Vehicle vehicle, GpsRequest gpsRequest) {
        VehicleStatus previousStatus = vehicle.getStatus();

        vehicle.setPreviousLat(vehicle.getLastLat());
        vehicle.setPreviousLng(vehicle.getLastLng());

        vehicle.setLastLat(gpsRequest.getLat());
        vehicle.setLastLng(gpsRequest.getLng());
        vehicle.setLastSeen(gpsRequest.getTimestamp());

        boolean positionChanged = hasPositionChanged(vehicle);

        if (positionChanged) {
            vehicle.setLastPositionChangedAt(gpsRequest.getTimestamp());
        }

        VehicleStatus newStatus = vehicleStatusService.calculateStatus(
                vehicle,
                gpsRequest.getTimestamp()
        );

        vehicle.setStatus(newStatus);

        if (previousStatus != newStatus) {
            telemetryEventService.registerEvent(
                    vehicle.getVehicleId(),
                    TelemetryEventType.STATUS_CHANGED,
                    "Vehículo " + vehicle.getVehicleId()
                            + " cambió de " + previousStatus
                            + " a " + newStatus,
                    previousStatus,
                    newStatus
            );
        }

        return vehicle;
    }

    private void saveGpsRecord(GpsRequest gpsRequest) {
        GpsRecord gpsRecord = GpsRecord.builder()
                .vehicleId(gpsRequest.getVehicleId())
                .lat(gpsRequest.getLat())
                .lng(gpsRequest.getLng())
                .timestamp(gpsRequest.getTimestamp())
                .build();

        gpsRecordRepository.save(gpsRecord);
    }

    private Vehicle getVehicleOrThrow(String vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    private boolean hasPositionChanged(Vehicle vehicle) {
        if (vehicle.getPreviousLat() == null || vehicle.getPreviousLng() == null) {
            return false;
        }

        return !vehicle.getPreviousLat().equals(vehicle.getLastLat())
                || !vehicle.getPreviousLng().equals(vehicle.getLastLng());
    }
}