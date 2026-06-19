package com.pancaran.master.feature.routeplan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteDetailDto {
    private int sequence;
    private String address;
    private Double lat;
    private Double lng;
    private Double distance;
}


