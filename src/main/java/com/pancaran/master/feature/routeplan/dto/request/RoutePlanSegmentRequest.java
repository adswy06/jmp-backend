package com.pancaran.master.feature.routeplan.dto.request;

import com.pancaran.master.feature.routeplan.dto.RouteCoordinatesDto;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutePlanSegmentRequest {
    private Integer sequence;
    private String address;
    private Double distance;
    private Double duration;
    private String instructions;
    private String description;
    @Valid
    private List<RouteCoordinatesDto> coordinates;
}
