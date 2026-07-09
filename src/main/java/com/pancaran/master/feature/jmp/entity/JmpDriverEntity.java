package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "t_jmp_driver")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpDriverEntity {
    @Id
    private String id;

    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;

    @Column(name = "driver_id")
    private String driverId;

    private Integer seqno;
}
