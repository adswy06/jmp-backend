package com.pancaran.master.feature.jmp.entity;

import com.pancaran.master.feature.tripplan.entity.BaseAuditEntity;
import com.pancaran.master.feature.tripplan.entity.master.CustomerEntity;
import com.pancaran.master.feature.tripplan.entity.master.ConsigneeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "t_jmp")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpEntity extends BaseAuditEntity {
    @Id
    private String id;

    @Column(name = "customer_id")
    private String customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private CustomerEntity customer;

    @Column(name = "consignee_id")
    private String consigneeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consignee_id", insertable = false, updatable = false)
    private ConsigneeEntity consignee;

    @Column(name = "commercial_route")
    private String commercialRoute;

    @Column(name = "reference_no")
    private String referenceNo;

    private String title;

    private String description;

    private String status;

    @Column(name = "is_notification_global")
    private Boolean isNotificationGlobal = false;
}
