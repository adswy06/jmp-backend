package com.pancaran.master.feature.tripplan.entity.master;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Entity
@Table(name = "em_customer")
@Immutable
@Getter
@Setter
public class CustomerView {
    @Id
    private String id;

    private String name;

    private String alias;

    private Boolean isactive;

    @Column(name = "alt_name")
    private String altName;

    private String prefix;

    private String suffix;

    private String description;

    private LocalDateTime updated;

    @Column(name = "updatedby")
    private String updatedBy;

    private Boolean isdeleted;

    private LocalDateTime deleted;

    private LocalDateTime lastsync;
}
