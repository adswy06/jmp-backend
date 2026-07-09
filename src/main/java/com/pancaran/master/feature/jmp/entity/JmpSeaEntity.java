package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_jmp_sea")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpSeaEntity {
    @Id
    private String id;

    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;

    @Column(name = "origin_port_id")
    private String originPortId;

    @Column(name = "destination_port_id")
    private String destinationPortId;

    @Column(name = "vessel_id")
    private String vesselId;

    private LocalDateTime etd;

    private LocalDateTime eta;
}
