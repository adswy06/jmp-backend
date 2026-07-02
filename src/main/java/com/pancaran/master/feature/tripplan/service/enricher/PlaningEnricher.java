package com.pancaran.master.feature.tripplan.service.enricher;

import com.pancaran.master.feature.region.repository.RegionRepository;
import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.GeofenceEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlaningEnricher {

    private final RouteRepository routeRepository;
    private final RegionRepository regionRepository;

    public void enrich(RouteAggregate aggregate) {
        List<RoutePointEntity> routePoints = aggregate.getRoutePoints();
        if (routePoints == null || routePoints.isEmpty()) {
            return;
        }

        // Enrich POIs from wilayah before routePoint enrichment
        if (aggregate.getPois() != null) {
            List<String> codesToEnrich = new java.util.ArrayList<>();
            for (PoiEntity poi : aggregate.getPois()) {
                if (poi.getName() == null || poi.getName().trim().isEmpty() || poi.getLat() == null || poi.getLng() == null) {
                    String code = poi.getRegionCode();
                    if (code != null && code.matches("\\d+") && (code.length() == 2 || code.length() == 4 || code.length() == 6 || code.length() == 10)) {
                        codesToEnrich.add(code);
                    }
                }
            }

            if (!codesToEnrich.isEmpty()) {
                Map<Integer, List<String>> groupedByLength = codesToEnrich.stream()
                        .distinct()
                        .collect(Collectors.groupingBy(String::length));

                Map<String, Object[]> detailsMap = new java.util.HashMap<>();
                for (Map.Entry<Integer, List<String>> entry : groupedByLength.entrySet()) {
                    List<Object[]> results = regionRepository.findRegionDetailsByCodes(entry.getValue(), entry.getKey());
                    for (Object[] row : results) {
                        if (row != null && row.length >= 4) {
                            detailsMap.put((String) row[0], row);
                        }
                    }
                }

                for (PoiEntity poi : aggregate.getPois()) {
                    String code = poi.getRegionCode();
                    Object[] details = code != null ? detailsMap.get(code) : null;
                    if (details != null) {
                        if (poi.getName() == null || poi.getName().trim().isEmpty()) {
                            poi.setName((String) details[1]);
                        }
                        if (poi.getAddress() == null || poi.getAddress().trim().isEmpty()) {
                            poi.setAddress((String) details[1]);
                        }
                        if (poi.getLng() == null && details[2] != null) {
                            poi.setLng(((Number) details[2]).doubleValue());
                        }
                        if (poi.getLat() == null && details[3] != null) {
                            poi.setLat(((Number) details[3]).doubleValue());
                        }
                    }
                    // Fallback name to avoid null value in m_poi.name constraint violation
                    if (poi.getName() == null || poi.getName().trim().isEmpty()) {
                        poi.setName("POI - " + poi.getId());
                    }
                }
            } else {
                for (PoiEntity poi : aggregate.getPois()) {
                    if (poi.getName() == null || poi.getName().trim().isEmpty()) {
                        poi.setName("POI - " + poi.getId());
                    }
                }
            }
        }

        List<String> poiIds = routePoints.stream()
                .map(RoutePointEntity::getPoiId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        List<PoiEntity> pois = routeRepository.findPoisByIds(poiIds);
        Map<String, PoiEntity> poiMap = pois.stream()
                .collect(Collectors.toMap(PoiEntity::getId, poi -> poi));

        Map<String, List<GeofenceEntity>> geofenceMap = aggregate.getGeofences() != null
                ? aggregate.getGeofences().stream()
                        .filter(g -> g.getPoiId() != null)
                        .collect(Collectors.groupingBy(GeofenceEntity::getPoiId))
                : java.util.Map.of();

        for (RoutePointEntity point : routePoints) {
            PoiEntity poi = poiMap.get(point.getPoiId());
            List<GeofenceEntity> gfs = geofenceMap.get(point.getPoiId());
            boolean hasZoneGeofence = gfs != null && gfs.stream()
                    .anyMatch(gf -> "ZONE".equalsIgnoreCase(gf.getShapeType()));

            if (poi != null) {
                point.setIszone(poi.getZone() != null || hasZoneGeofence);
            } else {
                point.setIszone(hasZoneGeofence);
            }

            point.setChecksum(calculateChecksum(point));
        }
    }

    private String calculateChecksum(RoutePointEntity point) {
        String data = (point.getPoiId() != null ? point.getPoiId() : "") + "_"
                + (point.getSeqno() != null ? point.getSeqno() : 0) + "_"
                + (point.getAlias() != null ? point.getAlias() : "") + "_"
                + (point.getAddress() != null ? point.getAddress() : "");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(data.hashCode());
        }
    }
}
