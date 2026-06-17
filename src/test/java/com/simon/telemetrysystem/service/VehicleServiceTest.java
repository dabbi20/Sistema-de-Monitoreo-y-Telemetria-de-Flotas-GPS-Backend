package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.dto.GpsRequest;
import com.simon.telemetrysystem.dto.VehicleResponse;
import com.simon.telemetrysystem.exception.VehicleNotFoundException;
import com.simon.telemetrysystem.mapper.GpsRecordMapper;
import com.simon.telemetrysystem.mapper.VehicleMapper;
import com.simon.telemetrysystem.model.*;
import com.simon.telemetrysystem.repository.GpsRecordRepository;
import com.simon.telemetrysystem.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private GpsRecordRepository gpsRecordRepository;

    @Mock
    private VehicleStatusService vehicleStatusService;

    @Mock
    private TelemetryEventService telemetryEventService;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private GpsRecordMapper gpsRecordMapper;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    void shouldCreateNewVehicleWhenVehicleDoesNotExist() {
        Instant timestamp = Instant.parse("2026-06-17T07:30:00Z");

        GpsRequest request = GpsRequest.builder()
                .vehicleId("VH-001")
                .lat(4.7110)
                .lng(-74.0721)
                .timestamp(timestamp)
                .build();

        VehicleResponse expectedResponse = VehicleResponse.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(timestamp)
                .status(VehicleStatus.DETENIDO)
                .build();

        when(vehicleRepository.findByVehicleId("VH-001"))
                .thenReturn(Optional.empty());

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(vehicleMapper.toResponse(any(Vehicle.class)))
                .thenReturn(expectedResponse);

        VehicleResponse response = vehicleService.processGpsData(request);

        assertEquals("VH-001", response.getVehicleId());
        assertEquals(VehicleStatus.DETENIDO, response.getStatus());

        verify(gpsRecordRepository).save(any(GpsRecord.class));
        verify(telemetryEventService).registerEvent(
                eq("VH-001"),
                eq(TelemetryEventType.VEHICLE_CREATED),
                anyString(),
                isNull(),
                eq(VehicleStatus.DETENIDO)
        );
    }

    @Test
    void shouldUpdateExistingVehicleAndChangeStatusToMoving() {
        Instant firstTimestamp = Instant.parse("2026-06-17T07:30:00Z");
        Instant secondTimestamp = Instant.parse("2026-06-17T07:30:20Z");

        Vehicle existingVehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(firstTimestamp)
                .lastPositionChangedAt(firstTimestamp)
                .status(VehicleStatus.DETENIDO)
                .build();

        GpsRequest request = GpsRequest.builder()
                .vehicleId("VH-001")
                .lat(4.7120)
                .lng(-74.0730)
                .timestamp(secondTimestamp)
                .build();

        VehicleResponse expectedResponse = VehicleResponse.builder()
                .vehicleId("VH-001")
                .lastLat(4.7120)
                .lastLng(-74.0730)
                .lastSeen(secondTimestamp)
                .status(VehicleStatus.EN_MOVIMIENTO)
                .build();

        when(vehicleRepository.findByVehicleId("VH-001"))
                .thenReturn(Optional.of(existingVehicle));

        when(vehicleStatusService.calculateStatus(any(Vehicle.class), eq(secondTimestamp)))
                .thenReturn(VehicleStatus.EN_MOVIMIENTO);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(vehicleMapper.toResponse(any(Vehicle.class)))
                .thenReturn(expectedResponse);

        VehicleResponse response = vehicleService.processGpsData(request);

        assertEquals(VehicleStatus.EN_MOVIMIENTO, response.getStatus());
        assertEquals(4.7120, response.getLastLat());

        verify(gpsRecordRepository).save(any(GpsRecord.class));
        verify(telemetryEventService).registerEvent(
                eq("VH-001"),
                eq(TelemetryEventType.STATUS_CHANGED),
                anyString(),
                eq(VehicleStatus.DETENIDO),
                eq(VehicleStatus.EN_MOVIMIENTO)
        );
    }

    @Test
    void shouldReturnVehicleByIdWhenExists() {
        Instant now = Instant.parse("2026-06-17T07:30:00Z");

        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(now)
                .lastPositionChangedAt(now)
                .status(VehicleStatus.DETENIDO)
                .build();

        VehicleResponse expectedResponse = VehicleResponse.builder()
                .vehicleId("VH-001")
                .lastLat(4.7110)
                .lastLng(-74.0721)
                .lastSeen(now)
                .status(VehicleStatus.DETENIDO)
                .build();

        when(vehicleRepository.findByVehicleId("VH-001"))
                .thenReturn(Optional.of(vehicle));

        when(vehicleStatusService.calculateStatus(any(Vehicle.class), any()))
                .thenReturn(VehicleStatus.DETENIDO);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(vehicleMapper.toResponse(any(Vehicle.class)))
                .thenReturn(expectedResponse);

        VehicleResponse response = vehicleService.getVehicleById("VH-001");

        assertEquals("VH-001", response.getVehicleId());
        assertEquals(VehicleStatus.DETENIDO, response.getStatus());
    }

    @Test
    void shouldThrowVehicleNotFoundExceptionWhenVehicleDoesNotExist() {
        when(vehicleRepository.findByVehicleId("VH-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.getVehicleById("VH-999")
        );
    }

    @Test
    void shouldDeleteVehicleWhenExists() {
        Vehicle vehicle = Vehicle.builder()
                .vehicleId("VH-001")
                .status(VehicleStatus.DETENIDO)
                .build();

        when(vehicleRepository.findByVehicleId("VH-001"))
                .thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle("VH-001");

        verify(telemetryEventService).registerEvent(
                eq("VH-001"),
                eq(TelemetryEventType.VEHICLE_DELETED),
                anyString(),
                eq(VehicleStatus.DETENIDO),
                isNull()
        );

        verify(vehicleRepository).deleteByVehicleId("VH-001");
    }
}