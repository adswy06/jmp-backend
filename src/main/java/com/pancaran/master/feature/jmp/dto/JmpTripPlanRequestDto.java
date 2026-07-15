package com.pancaran.master.feature.jmp.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({
    "id", "routeId", "seqno", "transportMode", "remarks",
    "routePoints", "routeDetails", "extraCosts", "drivers", "units", "sea", "air"
})
public class JmpTripPlanRequestDto {
    private String id;
    private String routeId;
    private Integer seqno;
    private String transportMode;
    private String remarks;

    // Custom Route Points
    private List<RoutePointRequestDto> routePoints;

    // Extra Costs
    private List<ExtraCostRequestDto> extraCosts;

    // Drivers
    private List<DriverRequestDto> drivers;

    // Units
    private List<UnitRequestDto> units;

    // Sea details
    private SeaRequestDto sea;

    // Air details
    private AirRequestDto air;
 
    private List<RouteDetailRequestDto> routeDetails;

    @Data
    @JsonPropertyOrder({
        "id", "routePointId", "poiId", "seqno", "alias", "address", "isCustom", "paths", "activities"
    })
    public static class RoutePointRequestDto {
        private String id;
        private String routePointId;
        private String poiId;
        private Integer seqno;
        private String alias;
        private String address;
        private Boolean isCustom;
        private Object paths;
        private List<ActivityRequestDto> activities;
    }

    @Data
    @JsonPropertyOrder({
        "id", "activityId", "activityName", "leadtime", "cost", "seqno", "remarks", "isNotification", "notes"
    })
    public static class ActivityRequestDto {
        private String id;
        private String activityId;
        private String activityName;
        private Integer leadtime;
        private BigDecimal cost;
        private Integer seqno;
        private String remarks;
        private Boolean isNotification;
        private String notes;
    }

    @Data
    @JsonPropertyOrder({
        "id", "name", "amount", "remarks"
    })
    public static class ExtraCostRequestDto {
        private String id;
        private String name;
        private BigDecimal amount;
        private String remarks;
    }

    @Data
    @JsonPropertyOrder({
        "id", "driverId", "seqno"
    })
    public static class DriverRequestDto {
        private String id;
        private String driverId;
        private Integer seqno;
    }

    @Data
    @JsonPropertyOrder({
        "id", "unitTypeId"
    })
    public static class UnitRequestDto {
        private String id;
        private String unitTypeId;
    }

    @Data
    @JsonPropertyOrder({
        "id", "originPortId", "destinationPortId", "vesselId", "etd", "eta"
    })
    public static class SeaRequestDto {
        private String id;
        private String originPortId;
        private String destinationPortId;
        private String vesselId;
        private LocalDateTime etd;
        private LocalDateTime eta;
    }

    @Data
    @JsonPropertyOrder({
        "id", "originAirportId", "destinationAirportId", "airline", "flightNo", "etd", "eta"
    })
    public static class AirRequestDto {
        private String id;
        private String originAirportId;
        private String destinationAirportId;
        private String airline;
        private String flightNo;
        private LocalDateTime etd;
        private LocalDateTime eta;
    }
 
    @Data
    @JsonPropertyOrder({
        "id", "seqNo", "startSeqNo", "endSeqNo", "remarks", "units"
    })
    public static class RouteDetailRequestDto {
        private String id;
        private Integer seqNo;
        private Integer startSeqNo;
        private Integer endSeqNo;
        private String remarks;
        private List<UnitRequestDto> units;
    }
}
