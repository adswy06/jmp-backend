package com.pancaran.master.feature.tripplan.dto.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonPropertyOrder({"poiId", "seqNo", "alias", "address", "geofence", "activities", "paths"})
public class RoutePointCreateDto implements Serializable {
    @NotBlank
    private String poiId;
    @NotNull
    private Integer seqNo;
    private String alias;
    private String address;
    private Object paths;
    @Valid
    private List<RoutePointGeofenceDto> geofence;
    @Valid
    private List<RouteActivityDto> activities;
}
