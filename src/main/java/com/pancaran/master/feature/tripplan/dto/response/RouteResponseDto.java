package com.pancaran.master.feature.tripplan.dto.response;

import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.util.List;

@Data
@JsonPropertyOrder({"route", "routePoints", "routeDetails"})
public class RouteResponseDto {
    private RouteEntity route;
    private List<RoutePointResponseDto> routePoints;
    private List<RouteSegmentResponseDto> routeDetails;

    @Data
    public static class RoutePointResponseDto {
        private String id;
        private String routeId;
        private String checksum;
        private String poiId;
        private PoiEntity poi;
        private Integer seqno;
        private String alias;
        private String address;
        private Object paths; // parsed JSON
        private Boolean iszone;
        private List<GeofenceEntity> geofences;
        private List<RouteActivityResponseDto> activities;
        private List<RoadHazardResponseDto> hazards;
    }

    @Data
    public static class RouteActivityResponseDto {
        private String activityId;
        private ActivityEntity activity; // master Activity details
        private Integer leadTime;
        private Double cost; // cost amount from ActivityCostEntity
        private List<ActivityExtraCostEntity> extraCosts;
    }

    @Data
    public static class RouteSegmentResponseDto {
        private String id;
        private String routeId;
        private String startRoutePointId;
        private String endRoutePointId;
        private Integer seqno;
        private String remarks;
        private List<RouteSegmentUnitEntity> units;
        private List<RoadHazardResponseDto> hazards;
    }

    @Data
    public static class RoadHazardResponseDto {
        private String id;
        private String name;
        private String hazardType;
        private Double latitude;
        private Double longitude;
        private Integer radius;
        private Double distanceFromRoute;
        private String severity;
    }
}
