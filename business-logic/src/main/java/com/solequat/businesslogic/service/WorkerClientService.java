package com.solequat.businesslogic.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.core.dto.CalculationDataHistoryDTO;
import com.core.dto.IntermediateResultDTO;
import com.core.dto.CalculationDataResultDTO;
import com.core.dto.PaymentDTO;

public interface WorkerClientService {

    IntermediateResultDTO calculateEquationFirstStage
        (MultipartFile matrixFile, MultipartFile vectorFile, String userId) throws IOException;

    CalculationDataResultDTO getEquationById(String id);

    List<CalculationDataHistoryDTO> getAllEquationsByUserId(String userId);

    byte[] getResultById(String id);
    byte[] getVectorById(String id);
    byte[] getMatrixById(String id);

    PaymentDTO getAllCalculationsByUserIdAndIsPaid(String userId);


    IntermediateResultDTO calculateEigenvaluesFirstStage
        (MultipartFile matrixFile, String userId) throws IOException;

    CalculationDataResultDTO getEigenvaluesById(String id);

    List<CalculationDataHistoryDTO> getAllEigenvaluesByUserId(String userId);

    byte[] getEigenvaluesResultById(String id);
    byte[] getEigenvaluesMatrixById(String id);
}
