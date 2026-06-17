package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.model.Vehicle;
import com.simon.telemetrysystem.model.VehicleStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VehicleStatusServiceTest {

    private final VehicleStatusService service = new VehicleStatusService();

    @Test
    void shouldReturnStoppedWhenVehicleIsNew() {
        Instant now = Instant.parse("2026-06-17T07:30:00Z");

        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(now)
                .lastPositionChangedAt(now)
                .status(VehicleStatus.DETENIDO)
                .build();

        VehicleStatus result = service.calculateStatus(vehicle, now);

        assertEquals(VehicleStatus.DETENIDO, result);
    }

    @Test
    void shouldReturnMovingWhenCoordinatesChanged() {
        Instant now = Instant.parse("2026-06-17T07:30:20Z");

        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .previousLat(4.7110)
                .previousLng(-74.0721)
                .lastLat(4.7120)
                .lastLng(-74.0730)
                .lastSeen(now)
                .lastPositionChangedAt(now)
                .status(VehicleStatus.DETENIDO)
                .build();

        VehicleStatus result = service.calculateStatus(vehicle, now);

        assertEquals(VehicleStatus.EN_MOVIMIENTO, result);
    }

    @Test
    void shouldReturnStoppedWhenSameCoordinatesForMoreThanSixtySeconds() {
        Instant positionChangedAt = Instant.parse("2026-06-17T07:30:00Z");
        Instant currentTimestamp = Instant.parse("2026-06-17T07:31:30Z");

        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .previousLat(4.7120)
                .previousLng(-74.0730)
                .lastLat(4.7120)
                .lastLng(-74.0730)
                .lastSeen(currentTimestamp)
                .lastPositionChangedAt(positionChangedAt)
                .status(VehicleStatus.EN_MOVIMIENTO)
                .build();

        VehicleStatus result = service.calculateStatus(vehicle, currentTimestamp);

        assertEquals(VehicleStatus.DETENIDO, result);
    }

    @Test
    void shouldReturnNoSignalWhenVehicleHasNotReportedForMoreThanTwoMinutes() {
        Instant lastSeen = Instant.parse("2026-06-17T07:30:00Z");
        Instant currentTimestamp = Instant.parse("2026-06-17T07:32:01Z");

        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(lastSeen)
                .lastPositionChangedAt(lastSeen)
                .status(VehicleStatus.EN_MOVIMIENTO)
                .build();

        VehicleStatus result = service.calculateStatus(vehicle, currentTimestamp);

        assertEquals(VehicleStatus.SIN_SENAL, result);
    }

    @Test
    void shouldThrowExceptionWhenVehicleIsNull() {
        Instant now = Instant.now();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateStatus(null, now)
        );
    }

    @Test
    void shouldThrowExceptionWhenTimestampIsNull() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .lastSeen(Instant.now())
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.calculateStatus(vehicle, null)
        );
    }
}