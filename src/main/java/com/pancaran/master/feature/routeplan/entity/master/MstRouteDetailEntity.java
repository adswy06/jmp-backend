package com.pancaran.master.feature.routeplan.entity.master;

import com.pancaran.master.feature.routeplan.entity.BaseRoutePlanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.util.List;

/* Detail titik mana saja */
@Getter
@Setter
@Entity
@Table(name = "m_route_detail")
public class MstRouteDetailEntity extends BaseRoutePlanEntity {
    @Id
    @Column(length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ref_id_route",
            nullable = false
    )
    private MstRouteEntity route;

    @Column(name = "address")
    private String address;

    @Column(nullable = false)
    private Double sequence;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    private Double distance;

    @Column(columnDefinition = "geography(Point,4326)")
    private Point geom;

    @OneToMany(
            mappedBy = "routeDetail",
            cascade = CascadeType.ALL
    )
    private List<MstRouteSegmentEntity> segments;
}
