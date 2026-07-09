package com.pancaran.master.feature.jmp.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
 
@Entity
@Table(name = "t_jmp_route_segment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class JmpRouteSegmentEntity {
    @Id
    private String id;
 
    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;
 
    @Column(name = "start_route_point_id")
    private String startRoutePointId;
 
    @Column(name = "end_route_point_id")
    private String endRoutePointId;
 
    private Integer seqno;
 
    private String remarks;
}
