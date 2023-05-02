package worker.service;

import java.util.List;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;

public interface EquationService {

    EquationIntermediateResultDTO calculateEquationFirstStage(EquationIdDTO equationIdDTO);

    EquationResultDTO getEquationById(String id);

    List<EquationHistoryDTO> getAllEquationsByUserId(String userId);
}
