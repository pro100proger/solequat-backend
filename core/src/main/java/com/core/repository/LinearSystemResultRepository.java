package com.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.core.entity.LinearSystemResult;

@Repository
public interface LinearSystemResultRepository extends MongoRepository<LinearSystemResult, String> {

}
