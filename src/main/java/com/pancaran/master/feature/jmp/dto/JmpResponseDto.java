package com.pancaran.master.feature.jmp.dto;

import com.pancaran.master.feature.jmp.entity.*;
import com.pancaran.master.feature.tripplan.entity.master.CustomerEntity;
import com.pancaran.master.feature.tripplan.entity.master.ConsigneeEntity;
import com.pancaran.master.feature.tripplan.dto.response.RouteResponseDto.RoadHazardResponseDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JmpResponseDto {
    private String id;
    private String customerId;
    private CustomerEntity customer;
    private String consigneeId;
    private ConsigneeEntity consignee;
    private String commercialRoute;
    private String referenceNo;
    private String title;
    private String description;
    private String status;
    private Boolean isNotificationGlobal;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    private List<JmpUnitEntity> units;
    private List<TripPlanResponseDto> tripPlans;

    @Data
    @JsonPropertyOrder({
        "id", "jmpId", "routeId", "seqno", "transportMode", "remarks", "createdAt",
        "routePoints", "routeDetails", "extraCosts", "drivers", "sea", "air"
    })
    public static class TripPlanResponseDto {
        private String id;
        private String jmpId;
        private String routeId;
        private Integer seqno;
        private String transportMode;
        private String remarks;
        private LocalDateTime createdAt;

        private List<RoutePointResponseDto> routePoints;
        private List<JmpExtraCostEntity> extraCosts;
        private List<JmpDriverEntity> drivers;
        private JmpSeaEntity sea;
        private JmpAirEntity air;
        private List<RouteDetailResponseDto> routeDetails;
    }

    @Data
    public static class RoutePointResponseDto {
        private String id;
        private String jmpTripPlanId;
        private String routePointId;
        private String poiId;
        private Integer seqno;
        private String alias;
        private String address;
        private Boolean isCustom;
        private String sourceType;
        private Object paths;
        private List<JmpActivityEntity> activities;
        private List<RoadHazardResponseDto> hazards;
    }

    @Data
    public static class RouteDetailResponseDto {
        private String id;
        private String jmpTripPlanId;
        private String startRoutePointId;
        private String endRoutePointId;
        private Integer seqno;
        private String remarks;
        private List<JmpRouteSegmentUnitEntity> units;
        private List<RoadHazardResponseDto> hazards;
    }
}
