package com.core.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EquationResultDTO {
    private long duration;
    private LocalDateTime endCalculation;
}
