package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;

@Entity
@Table(name = "t_jmp_extra_cost")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpExtraCostEntity {
    @Id
    private String id;

    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;

    private String name;

    private BigDecimal amount;

    private String remarks;
}
