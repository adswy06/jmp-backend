package com.pancaran.master.feature.tripplan.dto.response;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class PoiGeofenceDto implements Serializable {
    private String id;
    private String shapeType;
    private Integer radius;

    @JsonRawValue
    private String paths;
}
