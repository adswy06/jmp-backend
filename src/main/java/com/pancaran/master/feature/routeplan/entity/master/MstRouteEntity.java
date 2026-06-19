package com.pancaran.master.feature.routeplan.entity.master;

import com.pancaran.master.feature.routeplan.entity.BaseRoutePlanEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.util.List;

/* Master Route Header langsung Start - End */
@Getter
@Setter
@Entity
@Table(name = "m_route")
public class MstRouteEntity extends BaseRoutePlanEntity {
    @Id
    @Column(length = 40)
    private String id;

    @Column(nullable = false)
    private String alias;

    @Column(name = "location_from_id")
    private String locationFromId;

    @Column(name = "location_to_id")
    private String locationToId;
    @OneToMany(
            mappedBy = "route",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MstRouteDetailEntity> routeDetails;
}
