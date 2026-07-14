package com.pancaran.master.feature.region.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegionTreeDto implements Serializable {
    private String code;
    private String name;
    private Double areaKm2;
    private List<RegionTreeDto> regency;
    private List<RegionTreeDto> districs;
    private List<RegionTreeDto> vilages;
}
