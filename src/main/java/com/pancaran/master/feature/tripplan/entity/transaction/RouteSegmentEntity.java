package com.pancaran.master.feature.tripplan.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "m_route_segment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteSegmentEntity {
    @Id
    private String id;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "start_route_point_id")
    private String startRoutePointId;

    @Column(name = "end_route_point_id")
    private String endRoutePointId;

    private Integer seqno;

    private String remarks;
}
