package com.pancaran.master.feature.tripplan.entity.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "m_road_hazard")
@Getter
@Setter
public class RoadHazardEntity {
    @Id
    private String id;

    private String name;

    @Column(name = "hazard_type")
    private String hazardType;

    private Double latitude;

    private Double longitude;

    private Integer radius;

    private String severity;

    @Column(name = "is_active")
    private Boolean active = true;
}
