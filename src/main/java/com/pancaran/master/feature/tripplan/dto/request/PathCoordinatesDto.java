package com.pancaran.master.feature.tripplan.dto.request;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class PathCoordinatesDto implements Serializable {
    private List<List<Double>> paths;
    private String encodedPolyline;
    private Boolean usePolyline = true;
}
