package com.pancaran.master.feature.region.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RegencyDto {
    private Integer id;
    private String code;
    private String provinceCode;
    private String name;
    private String tipe;
    private BigDecimal areaKm2;
    private Integer jumlahPenduduk;
    private Integer jumlahKk;
    private Integer jumlahKec;
    private Integer jumlahDesa;
    private Integer jumlahKel;
    private BigDecimal kepadatan;
    private BigDecimal luasWilayah;

    @JsonRawValue
    private String geom;
}
