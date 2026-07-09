package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_jmp_trip_plan")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpTripPlanEntity {
    @Id
    private String id;

    @Column(name = "jmp_id", nullable = false)
    private String jmpId;

    @Column(name = "route_id")
    private String routeId;

    private Integer seqno;

    @Column(name = "transport_mode")
    private String transportMode;

    private String remarks;

    @CreationTimestamp
    @Column(name = "createdat", updatable = false)
    private LocalDateTime createdAt;
}
