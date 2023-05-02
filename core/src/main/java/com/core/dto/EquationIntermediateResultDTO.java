package com.core.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EquationIntermediateResultDTO {
    private String id;
    private String equationName;
    private LocalDateTime startCalculation;
}
