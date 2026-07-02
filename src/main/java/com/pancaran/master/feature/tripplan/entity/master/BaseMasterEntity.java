package com.pancaran.master.feature.tripplan.entity.master;

import com.pancaran.master.feature.tripplan.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseMasterEntity extends BaseAuditEntity {
    @Column(name = "isactive")
    private Boolean active = true;

    @Column(name = "isdeleted")
    private Boolean deleted = false;

    @Column(name = "deletedat")
    private LocalDateTime deletedAt;
}
