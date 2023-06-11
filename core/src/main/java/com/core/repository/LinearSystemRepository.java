package com.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.core.entity.LinearSystemRequest;

@Repository
public interface LinearSystemRepository extends MongoRepository<LinearSystemRequest, String> {

}
