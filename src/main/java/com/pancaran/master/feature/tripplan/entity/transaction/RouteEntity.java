package com.pancaran.master.feature.tripplan.entity.transaction;

import com.pancaran.master.feature.tripplan.entity.master.BaseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "m_route")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class RouteEntity extends BaseMasterEntity {
    @Id
    private String id;

    private String alias;

    private String name;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "journey_leadtime")
    private Integer journeyLeadTime;

    @Column(name = "basic_cost")
    private Double basicCost;
}
