package com.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.entity.Equation;

public interface WorkerClientRepository extends JpaRepository<Equation, String> {

    Equation getEquationById(String id);
}
