package com.core.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquationHistoryDTO {

    private String equationName;
    private LocalDateTime startCalculation;
    private LocalDateTime endCalculation;
    private long duration;

    public EquationHistoryDTO(EquationHistoryDTO permutationHistoryDTO) {
        this.equationName = permutationHistoryDTO.getEquationName();
        this.startCalculation = permutationHistoryDTO.getStartCalculation();
        this.endCalculation = permutationHistoryDTO.getEndCalculation();
        this.duration = permutationHistoryDTO.getDuration();
    }
}
