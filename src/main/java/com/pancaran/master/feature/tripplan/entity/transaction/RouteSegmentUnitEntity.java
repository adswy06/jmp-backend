package com.pancaran.master.feature.tripplan.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "m_route_segment_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteSegmentUnitEntity {
    @Id
    private String id;

    @Column(name = "route_segment_id")
    private String routeSegmentId;

    @Column(name = "unit_type_id")
    private String unitTypeId;
}
