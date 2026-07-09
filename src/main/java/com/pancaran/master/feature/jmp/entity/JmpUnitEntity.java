package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "t_jmp_unit")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpUnitEntity {
    @Id
    private String id;

    @Column(name = "jmp_id", nullable = false)
    private String jmpId;

    @Column(name = "unit_type_id")
    private String unitTypeId;
}
