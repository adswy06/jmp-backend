package com.pancaran.master.feature.jmp.entity;
 
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
 
@Entity
@Table(name = "t_jmp_route_segment_unit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class JmpRouteSegmentUnitEntity {
    @Id
    private String id;
 
    @Column(name = "jmp_route_segment_id", nullable = false)
    private String jmpRouteSegmentId;
 
    @Column(name = "unit_type_id")
    private String unitTypeId;
}
