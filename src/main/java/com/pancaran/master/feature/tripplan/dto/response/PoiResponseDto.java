package com.pancaran.master.feature.tripplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class PoiResponseDto implements Serializable {
    private String id;
    private String gpoiId;
    private String regionCode;
    private String zone;
    private String locationCategoryName;
    private String name;
    private String address;
    private Double lng;
    private Double lat;
    private Boolean isactive;
    private List<PoiGeofenceDto> geofences;
}
