package com.pancaran.master.feature.tripplan.entity.transaction;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "m_activity_extra_cost")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class ActivityExtraCostEntity {
    @Id
    private String id;

    @Column(name = "route_point_id")
    private String routePointId;

    @Column(name = "activity_id")
    private String activityId;

    private String name;

    private Double amount;
}
