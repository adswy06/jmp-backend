package com.pancaran.master.feature.tripplan.entity.master;

import com.pancaran.master.feature.tripplan.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "m_zone")
@Getter
@Setter
@AttributeOverrides({
    @AttributeOverride(name = "createdAt", column = @Column(name = "created_at", updatable = false)),
    @AttributeOverride(name = "updatedAt", column = @Column(name = "updated_at")),
    @AttributeOverride(name = "createdBy", column = @Column(name = "created_by")),
    @AttributeOverride(name = "updatedBy", column = @Column(name = "updated_by"))
})
public class ZoneEntity extends BaseAuditEntity {
    @Id
    private String id;

    private String name;

    private String address;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private String paths;
}
