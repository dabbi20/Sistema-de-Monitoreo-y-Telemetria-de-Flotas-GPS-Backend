package com.simon.telemetrysystem.controller;

import com.simon.telemetrysystem.dto.VehicleMetricsResponse;
import com.simon.telemetrysystem.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping
    public VehicleMetricsResponse getMetrics() {
        return metricsService.getMetrics();
    }
}