package com.pancaran.master.feature.routeplan.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RouteDetailRequest {
    private int sequence;
    private String address;
    private Double lat;
    private Double lng;
    private Double distance;
    private List<RoutePlanSegmentRequest> segments;
}
