package com.pancaran.master.feature.routeplan.entity.master;

import com.pancaran.master.feature.routeplan.entity.BaseRoutePlanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;

/* Segment detail jalan yg di lalui mana saja */
@Getter
@Setter
@Entity
@Table(name = "m_route_segment")
public class MstRouteSegmentEntity extends BaseRoutePlanEntity {
    @Id
    @Column(length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ref_id_route_detail",
            nullable = false
    )
    private MstRouteDetailEntity routeDetail;

    @Column(name = "sequence_no", nullable = false)
    private Integer sequenceNo;

    private String address;

    private Double distance;

    private Double duration;

    @Column(length = 1000)
    private String instructions;

    @Column(length = 1000)
    private String description;

    @Column(name = "segment_geom",
            columnDefinition = "geography(LineString,4326)")
    private LineString segmentGeom;
}
