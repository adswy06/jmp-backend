package com.pancaran.master.feature.tripplan.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointCreateDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointGeofenceDto;
import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.pancaran.master.feature.tripplan.mapper.transaction.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RouteAggregateMapper {

    private final RouteMapper routeMapper;

    private final RoutePointMapper routePointMapper;

    private final GeofenceMapper geofenceMapper;

    private final RouteActivityMapper routeActivityMapper;

    private final RouteSegmentMapper routeSegmentMapper;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RouteAggregate toAggregate(RouteCreateRequestDto request) {

        RouteEntity route =
                routeMapper.toEntity(request);

        List<RoutePointEntity> routePoints =
                routePointMapper.toEntities(request, route);

        // Generate UUID mapping for region codes
        Map<String, String> regionToUuidMap = new java.util.HashMap<>();
        for (RoutePointCreateDto rpDto : request.getRoutePoints()) {
            String originalPoiId = rpDto.getPoiId();
            if (originalPoiId != null && originalPoiId.matches("\\d+") && (originalPoiId.length() == 2 || originalPoiId.length() == 4 || originalPoiId.length() == 6 || originalPoiId.length() == 10)) {
                regionToUuidMap.put(originalPoiId, com.apik.core.common.helper.CoreUtil.createUUID());
            }
        }

        // Update the poiId in RoutePointEntity to use the generated UUID
        for (RoutePointEntity rp : routePoints) {
            String uuid = regionToUuidMap.get(rp.getPoiId());
            if (uuid != null) {
                rp.setPoiId(uuid);
            }
        }

        List<GeofenceEntity> geofences =
                geofenceMapper.toEntity(routePoints, request);

        List<ActivityLeadTimeEntity> leadTimes =
                routeActivityMapper.toLeadTime(routePoints, request);

        List<ActivityCostEntity> activityCosts =
                routeActivityMapper.toCost(routePoints, request);

        List<ActivityExtraCostEntity> extraCosts =
                routeActivityMapper.toExtraCosts(routePoints, request);

        List<RouteSegmentEntity> details =
                routeSegmentMapper.toSegment(route, routePoints, request);

        List<RouteSegmentUnitEntity> units =
                routeSegmentMapper.toUnit(details, request);

        // Map master POIs and Zones
        List<PoiEntity> pois = new ArrayList<>();
        Map<String, ZoneEntity> zoneMap = new java.util.HashMap<>();

        for (RoutePointCreateDto rpDto : request.getRoutePoints()) {
            PoiEntity poi = new PoiEntity();
            String originalPoiId = rpDto.getPoiId();
            String uuid = regionToUuidMap.get(originalPoiId);

            if (uuid != null) {
                poi.setId(uuid);
                poi.setRegionCode(originalPoiId);
                poi.setGpoiId(null);
            } else {
                poi.setId(originalPoiId);
                poi.setRegionCode(null);
            }
            poi.setName(rpDto.getAlias());
            poi.setAddress(rpDto.getAddress());
            poi.setIsactive(true);
            poi.setCreatedBy("SYSTEM");

            if (rpDto.getGeofence() != null) {
                for (RoutePointGeofenceDto gf : rpDto.getGeofence()) {
                    if ("ZONE".equalsIgnoreCase(gf.getShapeType())) {
                        String zoneId = null;
                        if (gf.getPaths() instanceof Map) {
                            Map<?, ?> pathsMap = (Map<?, ?>) gf.getPaths();
                            Object zoneIdObj = pathsMap.get("zoneId");
                            if (zoneIdObj != null) {
                                zoneId = zoneIdObj.toString();
                            }
                        }
                        if (zoneId == null) {
                            zoneId = uuid != null ? uuid : rpDto.getPoiId();
                        }

                        if (!zoneMap.containsKey(zoneId)) {
                            ZoneEntity zone = new ZoneEntity();
                            zone.setId(zoneId);
                            zone.setName(rpDto.getAlias() + " Zone");
                            zone.setAddress(rpDto.getAddress());
                            zone.setCreatedBy("SYSTEM");

                            if (gf.getPaths() != null) {
                                try {
                                    zone.setPaths(OBJECT_MAPPER.writeValueAsString(gf.getPaths()));
                                } catch (Exception e) {
                                    zone.setPaths(gf.getPaths().toString());
                                }
                            }
                            zoneMap.put(zoneId, zone);
                        }

                        poi.setZone(zoneId);
                    } else if ("RADIUS".equalsIgnoreCase(gf.getShapeType())) {
                        if (gf.getPaths() instanceof Map) {
                            Map<?, ?> pathsMap = (Map<?, ?>) gf.getPaths();
                            Object centerObj = pathsMap.get("center");
                            if (centerObj instanceof Map) {
                                Map<?, ?> centerMap = (Map<?, ?>) centerObj;
                                Object latObj = centerMap.get("lat");
                                Object lngObj = centerMap.get("lng");
                                if (latObj instanceof Number) {
                                    poi.setLat(((Number) latObj).doubleValue());
                                }
                                if (lngObj instanceof Number) {
                                    poi.setLng(((Number) lngObj).doubleValue());
                                }
                            }
                        }
                    }
                }
            }
            pois.add(poi);
        }

        return RouteAggregate.builder()
                .route(route)
                .routePoints(routePoints)
                .geofences(geofences)
                .leadTimes(leadTimes)
                .activityCosts(activityCosts)
                .extraCosts(extraCosts)
                .routeDetails(details)
                .routeUnits(units)
                .pois(pois)
                .zones(new ArrayList<>(zoneMap.values()))
                .build();
    }
}