package com.pancaran.master.feature.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "postal_code", schema = "wilayah")
@Getter
@Setter
public class PostalCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kode_desa", nullable = false, length = 10)
    private String kodeDesa;

    @Column(name = "kode_pos", length = 5)
    private String kodePos;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "confidence")
    private BigDecimal confidence;

    @Column(name = "sumber", columnDefinition = "text")
    private String sumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
