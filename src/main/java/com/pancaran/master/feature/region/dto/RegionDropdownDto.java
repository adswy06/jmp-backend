package com.pancaran.master.feature.region.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionDropdownDto implements Serializable {
    private String code;
    private String name;
    private String type; // "PROVINCE", "REGENCY", "DISTRICT", "VILLAGE"
    private String parentCode;
}
