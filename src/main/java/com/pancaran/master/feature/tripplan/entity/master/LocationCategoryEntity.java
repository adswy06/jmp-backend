package com.pancaran.master.feature.tripplan.entity.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "m_location_category")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class LocationCategoryEntity {
    @Id
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    private String description;

    @Column(name = "lastsync")
    private LocalDateTime lastSync;

    @ManyToMany
    @JoinTable(
        name = "zrel_location_category_activity",
        joinColumns = @JoinColumn(name = "location_category_id"),
        inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<ActivityEntity> activities = new HashSet<>();
}
