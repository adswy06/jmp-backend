package com.pancaran.master.feature.routeplan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteSegmentDto {
    private int sequence;
    private String address;
    private Double distance;
    private String duration;
    private String instruction;
    private String description;
    private List<RouteCoordinatesDto> coordinates;
}


