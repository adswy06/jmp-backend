package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;

@Entity
@Table(name = "t_jmp_activity")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpActivityEntity {
    @Id
    private String id;

    @Column(name = "jmp_route_point_id", nullable = false)
    private String jmpRoutePointId;

    @Column(name = "activity_id")
    private String activityId;

    @Column(name = "activity_name")
    private String activityName;

    private Integer leadtime;

    private BigDecimal cost;

    private Integer seqno;

    private String remarks;
 
    @Column(name = "is_notification")
    private Boolean isNotification = false;
 
    private String notes;
}
