package com.pancaran.master.feature.tripplan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoadHazardCreateDto {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Hazard type is required")
    private String hazardType;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    private Integer radius = 50;

    private String severity = "MEDIUM";

    private Boolean active = true;
}
