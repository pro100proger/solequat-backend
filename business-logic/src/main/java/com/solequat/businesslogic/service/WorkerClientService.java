package com.solequat.businesslogic.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.core.dto.EquationHistoryDTO;
import com.core.dto.EquationIntermediateResultDTO;
import com.core.dto.EquationResultDTO;
import com.core.dto.PaymentDTO;

public interface WorkerClientService {

    EquationIntermediateResultDTO calculateEquationFirstStage
        (MultipartFile matrixFile, MultipartFile vectorFile, String userId) throws IOException;

    EquationResultDTO getEquationById(String id);

    List<EquationHistoryDTO> getAllEquationsByUserId(String userId);

    byte[] getResultById(String id);
    byte[] getVectorById(String id);
    byte[] getMatrixById(String id);

    PaymentDTO getAllEquationsByUserIdAndIsPaid(String userId);
}
