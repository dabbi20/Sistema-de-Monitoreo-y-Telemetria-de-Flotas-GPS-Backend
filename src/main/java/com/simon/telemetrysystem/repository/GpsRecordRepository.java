package com.simon.telemetrysystem.repository;

import com.simon.telemetrysystem.model.GpsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GpsRecordRepository extends JpaRepository<GpsRecord, Long> {

    List<GpsRecord> findByVehicleIdOrderByTimestampDesc(String vehicleId);
}