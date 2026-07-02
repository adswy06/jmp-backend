package com.pancaran.master.feature.tripplan.mapper;

import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RouteAggregate {

    private RouteEntity route;

    private List<RoutePointEntity> routePoints;

    private List<GeofenceEntity> geofences;

    private List<ActivityLeadTimeEntity> leadTimes;

    private List<ActivityCostEntity> activityCosts;

    private List<ActivityExtraCostEntity> extraCosts;

    private List<RouteSegmentEntity> routeDetails;

    private List<RouteSegmentUnitEntity> routeUnits;

    private List<PoiEntity> pois;

    private List<ZoneEntity> zones;
}
