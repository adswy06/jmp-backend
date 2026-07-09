package com.pancaran.master.feature.tripplan.entity.master;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "m_activity")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class ActivityEntity {
    @Id
    private String id;

    @Column(name = "category_name")
    private String categoryName;

    private String name;

    private java.math.BigDecimal cost;

    @Column(name = "lead_time")
    private Integer leadTime;

    @ManyToMany(mappedBy = "activities")
    @JsonIgnore
    private Set<LocationCategoryEntity> locationCategories = new HashSet<>();
}
