package com.simon.telemetrysystem.controller;

import com.simon.telemetrysystem.dto.GpsRecordResponse;
import com.simon.telemetrysystem.dto.VehicleResponse;
import com.simon.telemetrysystem.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<VehicleResponse> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{vehicleId}")
    public VehicleResponse getVehicleById(
            @PathVariable String vehicleId
    ) {
        return vehicleService.getVehicleById(vehicleId);
    }

    @DeleteMapping("/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(
            @PathVariable String vehicleId
    ) {
        vehicleService.deleteVehicle(vehicleId);
    }

    @GetMapping("/{vehicleId}/records")
    public List<GpsRecordResponse> getVehicleRecords(
            @PathVariable String vehicleId
    ) {
        return vehicleService.getVehicleRecords(vehicleId);
    }
}