package com.simon.telemetrysystem.service;

import com.simon.telemetrysystem.dto.VehicleMetricsResponse;
import com.simon.telemetrysystem.model.Vehicle;
import com.simon.telemetrysystem.model.VehicleStatus;
import com.simon.telemetrysystem.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleStatusService vehicleStatusService;

    @InjectMocks
    private MetricsService metricsService;

    @Test
    void shouldCalculateVehicleMetrics() {
        Vehicle vehicle1 = Vehicle.builder()
                .vehicleId("VH-001")
                .lastSeen(Instant.now())
                .status(VehicleStatus.EN_MOVIMIENTO)
                .build();

        Vehicle vehicle2 = Vehicle.builder()
                .vehicleId("VH-002")
                .lastSeen(Instant.now())
                .status(VehicleStatus.DETENIDO)
                .build();

        Vehicle vehicle3 = Vehicle.builder()
                .vehicleId("VH-003")
                .lastSeen(Instant.now())
                .status(VehicleStatus.SIN_SENAL)
                .build();

        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle1, vehicle2, vehicle3));

        when(vehicleStatusService.calculateStatus(eq(vehicle1), any()))
                .thenReturn(VehicleStatus.EN_MOVIMIENTO);

        when(vehicleStatusService.calculateStatus(eq(vehicle2), any()))
                .thenReturn(VehicleStatus.DETENIDO);

        when(vehicleStatusService.calculateStatus(eq(vehicle3), any()))
                .thenReturn(VehicleStatus.SIN_SENAL);

        VehicleMetricsResponse response = metricsService.getMetrics();

        assertEquals(3, response.getTotalVehicles());
        assertEquals(1, response.getMovingVehicles());
        assertEquals(1, response.getStoppedVehicles());
        assertEquals(1, response.getNoSignalVehicles());

        verify(vehicleRepository, times(3)).save(any(Vehicle.class));
    }
}