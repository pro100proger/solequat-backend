package com.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.core.entity.EigenvaluesResult;

@Repository
public interface EigenvaluesResultRepository extends MongoRepository<EigenvaluesResult, String> {
}
