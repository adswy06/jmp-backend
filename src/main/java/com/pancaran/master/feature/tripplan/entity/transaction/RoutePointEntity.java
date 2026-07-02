package com.pancaran.master.feature.tripplan.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "m_route_point")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutePointEntity {
    @Id
    private String id;

    @Column(name = "ref_id_route")
    private String routeId;

    private String checksum;

    @Column(name = "poi_id")
    private String poiId;

    private Integer seqno;

    private String alias;

    private String address;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String paths;

    private Boolean iszone;
}
