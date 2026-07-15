package com.pancaran.master.feature.tripplan.service;

import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.PathCoordinatesDto;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.common.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancaran.master.feature.tripplan.dto.response.RouteResponseDto;
import java.util.*;
import java.util.stream.Collectors;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregateMapper;
import com.pancaran.master.feature.tripplan.service.enricher.PlaningEnricher;
import com.pancaran.master.feature.tripplan.service.persister.PlaningPersister;
import com.pancaran.master.feature.tripplan.service.processor.PlaningProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import com.apik.core.data.dto.SearchInput;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import com.pancaran.master.feature.tripplan.entity.master.ZoneEntity;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;

@Service
@RequiredArgsConstructor
public class PlaningService {

    private final RouteAggregateMapper mapper;
    private final PlaningEnricher enricher;
    private final PlaningProcessor processor;
    private final PlaningPersister persister;
    private final RouteRepository routeRepository;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public RouteResponseDto planRoute(RouteCreateRequestDto request) {
        RouteAggregate aggregate = mapper.toAggregate(request);

        if (aggregate.getRoute() != null && "DRAFT".equalsIgnoreCase(aggregate.getRoute().getStatus())) {
            aggregate.getRoute().setActive(false);
        }

        enricher.enrich(aggregate);

        processor.process(aggregate);

        persister.persist(aggregate);

        return toResponseDto(aggregate);
    }

    public Page<RouteEntity> getRoutePage(SearchInput input) {
        return routeRepository.findRoutePage(input);
    }

    public RouteResponseDto getRouteById(String id) {
        RouteEntity route = routeRepository.findRouteById(id);
        if (route == null) {
            throw new ApiException(404, "Route not found with id: " + id);
        }

        List<RoutePointEntity> routePoints = routeRepository.findRoutePointsByRouteId(id);
        List<GeofenceEntity> geofences = routeRepository.findGeofencesByRouteId(id);
        List<RouteSegmentEntity> routeDetails = routeRepository.findRouteSegmentsByRouteId(id);

        List<ActivityLeadTimeEntity> leadTimes = Collections.emptyList();
        List<ActivityCostEntity> activityCosts = Collections.emptyList();
        List<ActivityExtraCostEntity> extraCosts = Collections.emptyList();
        List<RouteSegmentUnitEntity> routeUnits = Collections.emptyList();
        List<PoiEntity> pois = Collections.emptyList();
        List<ZoneEntity> zones = Collections.emptyList();

        if (!routePoints.isEmpty()) {
            List<String> routePointIds = routePoints.stream().map(RoutePointEntity::getId).collect(Collectors.toList());
            leadTimes = routeRepository.findLeadTimesByRoutePointIds(routePointIds);
            activityCosts = routeRepository.findActivityCostsByRoutePointIds(routePointIds);
            extraCosts = routeRepository.findExtraCostsByRoutePointIds(routePointIds);

            List<String> poiIds = new ArrayList<>();
            for (RoutePointEntity rp : routePoints) {
                if (rp.getPoiId() != null && !rp.getPoiId().trim().isEmpty()) poiIds.add(rp.getPoiId());
            }
            for (GeofenceEntity g : geofences) {
                if (g.getPoiId() != null && !g.getPoiId().trim().isEmpty()) poiIds.add(g.getPoiId());
            }

            if (!poiIds.isEmpty()) {
                pois = routeRepository.findPoisByIdsNative(poiIds);
                List<String> zoneIds = pois.stream().map(PoiEntity::getZone).filter(Objects::nonNull).filter(z -> !z.trim().isEmpty()).distinct().collect(Collectors.toList());
                if (!zoneIds.isEmpty()) {
                    zones = routeRepository.findZonesByIdsNative(zoneIds);
                }
            }
        }

        if (!routeDetails.isEmpty()) {
            List<String> segmentIds = routeDetails.stream().map(RouteSegmentEntity::getId).collect(Collectors.toList());
            routeUnits = routeRepository.findRouteSegmentUnitsBySegmentIds(segmentIds);
        }

        RouteAggregate aggregate = RouteAggregate.builder()
                .route(route)
                .routePoints(routePoints)
                .geofences(geofences)
                .leadTimes(leadTimes)
                .activityCosts(activityCosts)
                .extraCosts(extraCosts)
                .routeDetails(routeDetails)
                .routeUnits(routeUnits)
                .pois(pois)
                .zones(zones)
                .build();

        return toResponseDto(aggregate);
    }

    public RouteResponseDto toResponseDto(RouteAggregate aggregate) {
        if (aggregate == null) return null;

        RouteResponseDto dto = new RouteResponseDto();
        dto.setRoute(aggregate.getRoute());

        List<RoutePointEntity> routePoints = aggregate.getRoutePoints() != null ? aggregate.getRoutePoints() : Collections.emptyList();
        List<GeofenceEntity> geofences = aggregate.getGeofences() != null ? aggregate.getGeofences() : Collections.emptyList();
        List<ActivityLeadTimeEntity> leadTimes = aggregate.getLeadTimes() != null ? aggregate.getLeadTimes() : Collections.emptyList();
        List<ActivityCostEntity> activityCosts = aggregate.getActivityCosts() != null ? aggregate.getActivityCosts() : Collections.emptyList();
        List<ActivityExtraCostEntity> extraCosts = aggregate.getExtraCosts() != null ? aggregate.getExtraCosts() : Collections.emptyList();
        List<RouteSegmentEntity> routeDetails = aggregate.getRouteDetails() != null ? aggregate.getRouteDetails() : Collections.emptyList();
        List<RouteSegmentUnitEntity> routeUnits = aggregate.getRouteUnits() != null ? aggregate.getRouteUnits() : Collections.emptyList();
        List<PoiEntity> pois = aggregate.getPois() != null ? aggregate.getPois() : Collections.emptyList();

        List<String> actIds = new ArrayList<>();
        leadTimes.forEach(lt -> { if (lt.getActivityId() != null) actIds.add(lt.getActivityId()); });
        activityCosts.forEach(ac -> { if (ac.getActivityId() != null) actIds.add(ac.getActivityId()); });
        List<ActivityEntity> activities = routeRepository.findActivitiesByIds(actIds.stream().distinct().collect(Collectors.toList()));
        Map<String, ActivityEntity> activityMap = activities.stream()
                .collect(Collectors.toMap(ActivityEntity::getId, java.util.function.Function.identity(), (a1, a2) -> a1));

        Map<String, PoiEntity> poiMap = pois.stream()
                .collect(Collectors.toMap(PoiEntity::getId, p -> p, (p1, p2) -> p1));

        Map<String, List<GeofenceEntity>> geofenceMap = geofences.stream()
                .collect(Collectors.groupingBy(g -> g.getPoiId() != null ? g.getPoiId() : ""));

        Map<String, List<ActivityLeadTimeEntity>> leadTimesMap = leadTimes.stream()
                .collect(Collectors.groupingBy(lt -> lt.getRoutePointId() != null ? lt.getRoutePointId() : ""));

        Map<String, List<ActivityCostEntity>> costsMap = activityCosts.stream()
                .collect(Collectors.groupingBy(c -> c.getRoutePointId() != null ? c.getRoutePointId() : ""));

        Map<String, List<ActivityExtraCostEntity>> extraCostsMap = extraCosts.stream()
                .collect(Collectors.groupingBy(ec -> (ec.getRoutePointId() != null ? ec.getRoutePointId() : "") + "-" + (ec.getActivityId() != null ? ec.getActivityId() : "")));

        Map<String, List<RouteSegmentUnitEntity>> segmentUnitsMap = routeUnits.stream()
                .collect(Collectors.groupingBy(u -> u.getRouteSegmentId() != null ? u.getRouteSegmentId() : ""));

        List<RouteResponseDto.RoutePointResponseDto> pointDtos = routePoints.stream().map(rp -> {
            RouteResponseDto.RoutePointResponseDto rpDto = new RouteResponseDto.RoutePointResponseDto();
            rpDto.setId(rp.getId());
            rpDto.setRouteId(rp.getRouteId());
            rpDto.setChecksum(rp.getChecksum());
            rpDto.setPoiId(rp.getPoiId());
            rpDto.setPoi(poiMap.get(rp.getPoiId()));
            rpDto.setSeqno(rp.getSeqno());
            rpDto.setAlias(rp.getAlias());
            rpDto.setAddress(rp.getAddress());
            rpDto.setIszone(rp.getIszone());

            if (rp.getPaths() != null && !rp.getPaths().trim().isEmpty()) {
                try {
                    rpDto.setPaths(OBJECT_MAPPER.readValue(rp.getPaths(), Object.class));
                } catch (Exception e) {
                    rpDto.setPaths(rp.getPaths());
                }
            }

            rpDto.setGeofences(geofenceMap.getOrDefault(rp.getPoiId(), Collections.emptyList()));

            PoiEntity poi = poiMap.get(rp.getPoiId());
            if (poi != null && poi.getLat() != null && poi.getLng() != null) {
                rpDto.setHazards(routeRepository.findHazardsByPoi(poi.getLat(), poi.getLng(), 50.0));
            } else {
                rpDto.setHazards(Collections.emptyList());
            }

            List<ActivityLeadTimeEntity> pointLeadTimes = leadTimesMap.getOrDefault(rp.getId(), Collections.emptyList());
            List<ActivityCostEntity> pointCosts = costsMap.getOrDefault(rp.getId(), Collections.emptyList());

            Set<String> pointActIds = new LinkedHashSet<>();
            pointLeadTimes.forEach(lt -> pointActIds.add(lt.getActivityId()));
            pointCosts.forEach(c -> pointActIds.add(c.getActivityId()));

            List<RouteResponseDto.RouteActivityResponseDto> actDtos = pointActIds.stream().map(actId -> {
                RouteResponseDto.RouteActivityResponseDto actDto = new RouteResponseDto.RouteActivityResponseDto();
                actDto.setActivityId(actId);
                actDto.setActivity(activityMap.get(actId));

                Integer ltVal = pointLeadTimes.stream()
                        .filter(lt -> actId.equals(lt.getActivityId()))
                        .map(ActivityLeadTimeEntity::getLeadtime)
                        .findFirst().orElse(null);
                actDto.setLeadTime(ltVal);

                Double costVal = pointCosts.stream()
                        .filter(c -> actId.equals(c.getActivityId()))
                        .map(ActivityCostEntity::getAmount)
                        .findFirst().orElse(null);
                actDto.setCost(costVal);

                actDto.setExtraCosts(extraCostsMap.getOrDefault(rp.getId() + "-" + actId, Collections.emptyList()));

                return actDto;
            }).collect(Collectors.toList());

            rpDto.setActivities(actDtos);
            return rpDto;
        }).collect(Collectors.toList());
        dto.setRoutePoints(pointDtos);

        List<RouteResponseDto.RouteSegmentResponseDto> detailDtos = routeDetails.stream().map(seg -> {
            RouteResponseDto.RouteSegmentResponseDto segDto = new RouteResponseDto.RouteSegmentResponseDto();
            segDto.setId(seg.getId());
            segDto.setRouteId(seg.getRouteId());
            segDto.setStartRoutePointId(seg.getStartRoutePointId());
            segDto.setEndRoutePointId(seg.getEndRoutePointId());
            segDto.setSeqno(seg.getSeqno());
            segDto.setRemarks(seg.getRemarks());
            segDto.setUnits(segmentUnitsMap.getOrDefault(seg.getId(), Collections.emptyList()));

            RoutePointEntity endPoint = routePoints.stream()
                    .filter(p -> p.getId().equals(seg.getEndRoutePointId()))
                    .findFirst().orElse(null);

            if (endPoint != null && endPoint.getPaths() != null) {
                String lineString = convertPathsToLineString(endPoint.getPaths());
                if (lineString != null) {
                    segDto.setHazards(routeRepository.findHazardsByLineString(lineString));
                } else {
                    segDto.setHazards(Collections.emptyList());
                }
            } else {
                segDto.setHazards(Collections.emptyList());
            }

            return segDto;
        }).collect(Collectors.toList());
        dto.setRouteDetails(detailDtos);

        return dto;
    }

    public List<RouteResponseDto.RoadHazardResponseDto> detectHazards(PathCoordinatesDto request) {
        if (request == null) {
            return Collections.emptyList();
        }

        boolean usePolyline = request.getUsePolyline() != null && request.getUsePolyline();
        List<List<Double>> paths = request.getPaths();

        if (usePolyline) {
            if (request.getEncodedPolyline() == null || request.getEncodedPolyline().trim().isEmpty()) {
                throw new ApiException("Encoded polyline is mandatory when usePolyline is true");
            }
            paths = decodePolyline(request.getEncodedPolyline());
        } else {
            if (paths == null || paths.isEmpty()) {
                throw new ApiException("Paths array is mandatory when usePolyline is false");
            }
        }

        if (paths == null || paths.isEmpty()) {
            return Collections.emptyList();
        }

        // If usePolyline is false, only detect hazards at endpoint point locations (all coordinates inside paths list)
        if (!usePolyline) {
            List<RouteResponseDto.RoadHazardResponseDto> pointHazards = new ArrayList<>();
            for (List<Double> pt : paths) {
                if (pt != null && pt.size() >= 2) {
                    pointHazards.addAll(routeRepository.findHazardsByPoi(pt.get(1), pt.get(0), 100.0));
                }
            }
            // Distinct list by equals/hashCode of DTO
            return pointHazards.stream().distinct().collect(Collectors.toList());
        }

        // Otherwise, detect along the entire polyline LineString
        String lineString = convertPathsToLineString(paths);
        if (lineString == null) {
            return Collections.emptyList();
        }
        return routeRepository.findHazardsByLineString(lineString);
    }

    private List<List<Double>> decodePolyline(String encoded) {
        List<List<Double>> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        try {
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                List<Double> p = new ArrayList<>();
                p.add((double) lng / 1E5); // X coordinate (longitude)
                p.add((double) lat / 1E5); // Y coordinate (latitude)
                poly.add(p);
            }
        } catch (Exception e) {
            // Return whatever we managed to decode or empty if malformed
        }
        return poly;
    }

    public String convertPathsToLineString(Object pathsObj) {
        if (pathsObj == null) return null;
        try {
            List<?> coords = null;
            String polyline = null;

            Object parsedObj = pathsObj;
            if (pathsObj instanceof String) {
                String pathsStr = (String) pathsObj;
                if (pathsStr.trim().isEmpty()) return null;
                try {
                    parsedObj = OBJECT_MAPPER.readValue(pathsStr, Object.class);
                } catch (Exception e) {
                    // Fallback if not valid JSON
                }
            }

            if (parsedObj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) parsedObj;
                Object polyObj = map.get("encodePolyline");
                if (polyObj == null) {
                    polyObj = map.get("encodedPolyline");
                }
                if (polyObj instanceof String) {
                    polyline = (String) polyObj;
                }
            } else if (parsedObj instanceof List) {
                coords = (List<?>) parsedObj;
            }

            if (polyline != null && !polyline.trim().isEmpty()) {
                coords = decodePolyline(polyline);
            }

            if (coords == null || coords.isEmpty()) return null;

            StringBuilder sb = new StringBuilder("LINESTRING(");
            for (int i = 0; i < coords.size(); i++) {
                Object c = coords.get(i);
                if (c instanceof List) {
                    List<?> pt = (List<?>) c;
                    if (pt.size() >= 2) {
                        if (i > 0) sb.append(", ");
                        sb.append(pt.get(0)).append(" ").append(pt.get(1));
                    }
                }
            }
            sb.append(")");
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public Object testDatabaseQuery() {
        String lineString = "LINESTRING(106.88456 -6.10123, 106.90000 -6.12000, 106.95000 -6.18000, 107.01234 -6.24213)";
        java.util.Map<String, Object> debug = new java.util.HashMap<>();
        try {
            debug.put("inputPath", lineString);
            
            Integer totalHazards = routeRepository.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM m_road_hazard", Integer.class);
            debug.put("totalHazards", totalHazards);

            List<?> closest = routeRepository.getJdbcTemplate().query(
                "SELECT id, name, radius, severity, latitude, longitude, " +
                "ST_Distance(ST_Transform(geom, 3857), ST_Transform(ST_GeomFromText(?, 4326), 3857)) as distance_meters " +
                "FROM m_road_hazard " +
                "ORDER BY distance_meters ASC LIMIT 5",
                (rs, rowNum) -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("name", rs.getString("name"));
                    map.put("radius", rs.getInt("radius"));
                    map.put("distance_meters", rs.getDouble("distance_meters"));
                    map.put("lat", rs.getDouble("latitude"));
                    map.put("lng", rs.getDouble("longitude"));
                    return map;
                },
                lineString
            );
            debug.put("closestHazards", closest);

            List<?> matched = routeRepository.getJdbcTemplate().query(
                "SELECT id, name, radius, " +
                "ST_Distance(ST_Transform(geom, 3857), ST_Transform(ST_GeomFromText(?, 4326), 3857)) as distance_meters " +
                "FROM m_road_hazard " +
                "WHERE is_active = true " +
                "AND ST_DWithin(ST_Transform(geom, 3857), ST_Transform(ST_GeomFromText(?, 4326), 3857), radius) " +
                "ORDER BY distance_meters ASC",
                (rs, rowNum) -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", rs.getString("id"));
                    map.put("name", rs.getString("name"));
                    map.put("radius", rs.getInt("radius"));
                    map.put("distance_meters", rs.getDouble("distance_meters"));
                    return map;
                },
                lineString, lineString
            );
            debug.put("matchedHazards", matched);

        } catch (Exception e) {
            debug.put("error", e.getMessage());
        }
        return debug;
    }
}
