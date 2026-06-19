package com.pancaran.master.feature.routeplan.dto.request;

import com.pancaran.master.feature.routeplan.dto.RouteDetailDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RoutePlanRequest implements Serializable {
    @NotBlank
    private String routeName;
    private String locationFromId;
    private String locationToId;
    @Valid
    private List<RouteDetailRequest> points;
}
