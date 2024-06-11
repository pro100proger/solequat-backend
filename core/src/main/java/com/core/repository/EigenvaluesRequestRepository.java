package com.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.core.entity.EigenvaluesRequest;

@Repository
public interface EigenvaluesRequestRepository extends MongoRepository<EigenvaluesRequest, String> {
}
