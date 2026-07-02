package com.pancaran.master.feature.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "kecamatan", schema = "wilayah")
@Getter
@Setter
public class DistrictEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "kode_kec", nullable = false, unique = true, length = 6)
    private String kodeKec;

    @Column(name = "kode_kab", nullable = false, length = 4)
    private String kodeKab;

    @Column(name = "nama_kecamatan", nullable = false, length = 100)
    private String namaKecamatan;

    @Column(name = "area_km2")
    private BigDecimal areaKm2;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "jumlah_penduduk")
    private Integer jumlahPenduduk;

    @Column(name = "jumlah_kk")
    private Integer jumlahKk;

    @Column(name = "jumlah_desa")
    private Integer jumlahDesa;

    @Column(name = "jumlah_kel")
    private Integer jumlahKel;

    @Column(name = "kepadatan")
    private BigDecimal kepadatan;

    @Column(name = "luas_wilayah")
    private BigDecimal luasWilayah;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
