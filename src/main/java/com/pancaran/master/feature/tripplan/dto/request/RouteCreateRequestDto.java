package com.pancaran.master.feature.tripplan.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({
    "name", "alias", "status", "distanceKm", "journeyLeadTime", "basicCost",
    "routePoints", "routeDetails"
})
public class RouteCreateRequestDto implements Serializable {
    @NotBlank
    private String name;
    private String alias;
    private Double distanceKm;
    private Integer journeyLeadTime;
    private Double basicCost;
    @Valid
    @NotEmpty
    private List<RoutePointCreateDto> routePoints;
    @Valid
    @NotEmpty
    private List<RouteSegmentDto> routeDetails;
    private String status;
}
