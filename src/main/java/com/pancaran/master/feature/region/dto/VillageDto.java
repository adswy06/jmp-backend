package com.pancaran.master.feature.region.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class VillageDto {
    private Integer id;
    private String code;
    private String districtCode;
    private String name;
    private String tipe;
    private BigDecimal areaKm2;
    private Integer jumlahPenduduk;
    private String pulau;
    private String jangkauan;

    @JsonRawValue
    private String geom;
}
