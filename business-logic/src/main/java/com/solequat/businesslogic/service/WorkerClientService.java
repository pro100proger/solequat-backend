package com.solequat.businesslogic.service;

import java.util.List;

import com.core.dto.EquationDTO;
import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;


public interface WorkerClientService {

    EquationIntermediateResultDTO calculateEquationFirstStage(EquationDTO equationDTO, String userId);

    EquationResultDTO getEquationById(String id);

    List<EquationHistoryDTO> getAllEquationsByUserId(String userId);
}
