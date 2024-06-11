package com.core.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Eigenvalues {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    @Column(name="duration")
    private Long duration;

    @Column(name="start_calculation")
    private LocalDateTime startCalculation;
    @Column(name="end_calculation")
    private LocalDateTime endCalculation;

    @Column(name="data_id")
    private String dataId;
    @Column(name="result_id")
    private String resultId;


    @Column(name="is_paid")
    private Boolean isPaid = false;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_logs_user"))
    private User user;

    public Eigenvalues(LocalDateTime startCalculation) {
        this.startCalculation = startCalculation;
    }
}
