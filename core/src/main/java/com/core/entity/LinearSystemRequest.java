package com.core.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document
public class LinearSystemRequest {

    @Id
    private String id;
    @Field
    private double[][] matrix;
    @Field
    private double[] vector;

}
