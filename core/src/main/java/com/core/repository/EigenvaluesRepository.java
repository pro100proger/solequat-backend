package com.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.entity.Eigenvalues;

public interface EigenvaluesRepository extends JpaRepository<Eigenvalues, String> {
    Eigenvalues getEigenvaluesById(String id);
    Optional<List<Eigenvalues>> getAllEigenvaluesByUserId(String userId);

    Optional<List<Eigenvalues>> getAllEigenvaluesByUserIdAndIsPaid(String userId, Boolean isPaid);
}
