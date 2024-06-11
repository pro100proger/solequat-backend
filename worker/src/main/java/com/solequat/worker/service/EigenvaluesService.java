package com.solequat.worker.service;

import java.util.List;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.IntermediateResultDTO;

public interface EigenvaluesService {

    IntermediateResultDTO calculateEigenvaluesFirstStage(CalculationDataIdDTO eigenvaluesIdDTO) throws Exception;

    CalculationDataResultDTO getEigenvaluesById(String id);

    List<CalculationDataHistoryDTO> getAllEigenvaluesByUserId(String userId);

    byte[] getEigenvaluesResultById(String id) throws Exception;
    byte[] getEigenvaluesMatrixById(String id) throws Exception;
}
