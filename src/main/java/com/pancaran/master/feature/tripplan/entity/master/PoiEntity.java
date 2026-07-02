package com.pancaran.master.feature.tripplan.entity.master;

import com.pancaran.master.feature.tripplan.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "m_poi")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class PoiEntity extends BaseAuditEntity {
    @Id
    private String id;

    @Column(name = "gpoi_id")
    private String gpoiId;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "zone_id")
    private String zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "location_category",
            referencedColumnName = "category_name")
    private LocationCategoryEntity locationCategory;

    private String name;

    private String address;

    private Double lng;

    private Double lat;

    private Boolean isactive;
}
