package worker.service;

import java.util.List;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIdDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;

public interface EquationService {

    EquationIntermediateResultDTO calculateEquationFirstStage(EquationIdDTO equationIdDTO) throws Exception;

    EquationResultDTO getEquationById(String id);

    List<EquationHistoryDTO> getAllEquationsByUserId(String userId);

    byte[] getResultById(String id) throws Exception;
    byte[] getVectorById(String id) throws Exception;
    byte[] getMatrixById(String id) throws Exception;
}
