package com.pancaran.master.feature.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "desa", schema = "wilayah")
@Getter
@Setter
public class VillageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kode_desa", nullable = false, unique = true, length = 10)
    private String kodeDesa;

    @Column(name = "kode_kec", nullable = false, length = 6)
    private String kodeKec;

    @Column(name = "nama_desa", nullable = false, length = 100)
    private String namaDesa;

    @Column(name = "tipe", nullable = false, length = 15)
    private String tipe;

    @Column(name = "area_km2")
    private BigDecimal areaKm2;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "jumlah_penduduk")
    private Integer jumlahPenduduk;

    @Column(name = "pulau", length = 100)
    private String pulau;

    @Column(name = "jangkauan", length = 100)
    private String jangkauan;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
