package com.core.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class IntermediateResultDTO {
    private String id;
    private LocalDateTime startCalculation;
}
