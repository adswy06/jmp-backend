package com.pancaran.master.feature.tripplan.dto.request;

import lombok.Data;

@Data
public class RoadHazardUpdateDto {
    private String name;
    private String hazardType;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private String severity;
    private Boolean active;
}
