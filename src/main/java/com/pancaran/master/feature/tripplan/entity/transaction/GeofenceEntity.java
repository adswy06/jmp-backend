package com.pancaran.master.feature.tripplan.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "geofence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class GeofenceEntity {
    @Id
    private String id;

    @Column(name = "poi_id")
    private String poiId;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "shapetype")
    private String shapeType;

    private Integer radius;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String paths;
}
