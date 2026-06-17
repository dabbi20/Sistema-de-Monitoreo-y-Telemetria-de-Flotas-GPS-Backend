package com.simon.telemetrysystem.controller;

import com.simon.telemetrysystem.dto.GpsRequest;
import com.simon.telemetrysystem.dto.VehicleResponse;
import com.simon.telemetrysystem.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gps")
@RequiredArgsConstructor
public class GpsController {

    private final VehicleService vehicleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse processGpsData(
            @Valid @RequestBody GpsRequest gpsRequest
    ) {
        return vehicleService.processGpsData(gpsRequest);
    }
}