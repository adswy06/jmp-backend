package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "t_jmp_route_point")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpRoutePointEntity {
    @Id
    private String id;

    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;

    @Column(name = "route_point_id")
    private String routePointId;

    @Column(name = "poi_id", nullable = false)
    private String poiId;

    private Integer seqno;

    private String alias;

    private String address;

    @Column(name = "iscustom")
    private Boolean isCustom;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String paths;
}
