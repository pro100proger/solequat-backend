package com.solequat.worker.service;

import java.util.List;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.CalculationDataIdDTO;
import com.core.dto.IntermediateResultDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.PaymentDTO;

public interface EquationService {

    IntermediateResultDTO calculateEquationFirstStage(CalculationDataIdDTO equationIdDTO) throws Exception;

    CalculationDataResultDTO getEquationById(String id);

    List<CalculationDataHistoryDTO> getAllEquationsByUserId(String userId);

    byte[] getResultById(String id) throws Exception;
    byte[] getVectorById(String id) throws Exception;
    byte[] getMatrixById(String id) throws Exception;

    PaymentDTO getAllEquationsByUserIdAndIsPaid(String userId);
}
