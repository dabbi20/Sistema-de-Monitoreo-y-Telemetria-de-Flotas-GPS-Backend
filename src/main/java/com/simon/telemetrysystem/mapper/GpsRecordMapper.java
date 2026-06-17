package com.simon.telemetrysystem.mapper;

import com.simon.telemetrysystem.dto.GpsRecordResponse;
import com.simon.telemetrysystem.model.GpsRecord;
import org.springframework.stereotype.Component;

@Component
public class GpsRecordMapper {

    public GpsRecordResponse toResponse(GpsRecord gpsRecord) {
        if (gpsRecord == null) {
            return null;
        }

        return GpsRecordResponse.builder()
                .vehicleId(gpsRecord.getVehicleId())
                .lat(gpsRecord.getLat())
                .lng(gpsRecord.getLng())
                .timestamp(gpsRecord.getTimestamp())
                .build();
    }
}