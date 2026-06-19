package com.pancaran.master.feature.routeplan.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteHeaderDto {
    private String id;
    private String routeName;
    private String locTo;
    private String locFrom;
    private List<RouteDetailDto> points;
}
