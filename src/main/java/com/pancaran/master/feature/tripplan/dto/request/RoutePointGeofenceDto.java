package com.pancaran.master.feature.tripplan.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class RoutePointGeofenceDto implements Serializable {
    private String shapeType;
    private Integer radius;
    private Object paths;
}
