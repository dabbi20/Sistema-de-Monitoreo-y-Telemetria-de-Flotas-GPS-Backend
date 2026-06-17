package com.simon.telemetrysystem.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String message;

    private int status;

    private Instant timestamp;
}