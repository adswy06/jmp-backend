package com.pancaran.master.feature.tripplan.service;

import com.pancaran.master.common.ApiException;
import com.pancaran.master.feature.tripplan.dto.response.PoiGeofenceDto;
import com.pancaran.master.feature.tripplan.dto.response.PoiResponseDto;
import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.GeofenceEntity;
import com.pancaran.master.feature.tripplan.repository.PoiRepository;
import com.pancaran.master.feature.region.repository.RegionJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "jmp-dbTransactionManager", readOnly = true)
public class PoiService {

    private final PoiRepository repository;
    private final RegionJdbcRepository regionJdbcRepository;

    public List<PoiResponseDto> getPois(String name, String category, Boolean isactive) {
        List<PoiEntity> pois = repository.findPois(name, category, isactive);
        if (pois.isEmpty()) {
            return List.of();
        }

        List<String> poiIds = pois.stream().map(PoiEntity::getId).collect(Collectors.toList());
        List<GeofenceEntity> geofences = repository.findGeofencesByPoiIds(poiIds);
        Map<String, List<GeofenceEntity>> geofenceMap = geofences.stream()
                .collect(Collectors.groupingBy(GeofenceEntity::getPoiId));

        // Fetch PostGIS boundaries in batch for wilayah POIs that don't have database geofences
        List<String> regionCodesToFetch = pois.stream()
                .filter(p -> p.getRegionCode() != null && geofenceMap.getOrDefault(p.getId(), List.of()).isEmpty())
                .map(PoiEntity::getRegionCode)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> regionGeoms = fetchRegionGeometries(regionCodesToFetch);

        return pois.stream()
                .map(poi -> toResponseDto(poi, geofenceMap.getOrDefault(poi.getId(), List.of()), regionGeoms))
                .collect(Collectors.toList());
    }

    public PoiResponseDto getPoiById(String id) {
        PoiEntity entity = repository.findPoiById(id)
                .orElseThrow(() -> new ApiException(404, "POI not found with id: " + id));

        List<GeofenceEntity> geofences = repository.findGeofencesByPoiIds(List.of(id));

        Map<String, String> regionGeoms = new HashMap<>();
        if (geofences.isEmpty() && entity.getRegionCode() != null) {
            regionGeoms = fetchRegionGeometries(List.of(entity.getRegionCode()));
        }

        return toResponseDto(entity, geofences, regionGeoms);
    }

    private Map<String, String> fetchRegionGeometries(List<String> codes) {
        Map<String, String> regionGeoms = new HashMap<>();
        if (codes == null || codes.isEmpty()) {
            return regionGeoms;
        }

        Map<Integer, List<String>> groupedByLength = codes.stream()
                .collect(Collectors.groupingBy(String::length));

        for (Map.Entry<Integer, List<String>> entry : groupedByLength.entrySet()) {
            int length = entry.getKey();
            List<String> lengthCodes = entry.getValue();
            if (length == 10) {
                regionGeoms.putAll(regionJdbcRepository.findVillageGeoms(lengthCodes));
            } else if (length == 6) {
                regionGeoms.putAll(regionJdbcRepository.findDistrictGeoms(lengthCodes));
            } else if (length == 4) {
                regionGeoms.putAll(regionJdbcRepository.findRegencyGeoms(lengthCodes));
            } else if (length == 2) {
                regionGeoms.putAll(regionJdbcRepository.findProvinceGeoms(lengthCodes));
            }
        }
        return regionGeoms;
    }

    private PoiResponseDto toResponseDto(PoiEntity entity, List<GeofenceEntity> geofences, Map<String, String> regionGeoms) {
        if (entity == null) return null;

        List<PoiGeofenceDto> geofenceDtos = new ArrayList<>();
        if (geofences != null && !geofences.isEmpty()) {
            for (GeofenceEntity gf : geofences) {
                geofenceDtos.add(PoiGeofenceDto.builder()
                        .id(gf.getId())
                        .shapeType(gf.getShapeType())
                        .radius(gf.getRadius())
                        .paths(gf.getPaths())
                        .build());
            }
        } else if (entity.getRegionCode() != null) {
            // Fallback to dynamic wilayah boundary geofence
            String geom = regionGeoms.get(entity.getRegionCode());
            if (geom != null) {
                geofenceDtos.add(PoiGeofenceDto.builder()
                        .id("dynamic-" + entity.getId())
                        .shapeType("ZONE")
                        .radius(0)
                        .paths(geom)
                        .build());
            }
        }

        return PoiResponseDto.builder()
                .id(entity.getId())
                .gpoiId(entity.getGpoiId())
                .regionCode(entity.getRegionCode())
                .zone(entity.getZone())
                .locationCategoryName(entity.getLocationCategory() != null ? entity.getLocationCategory().getCategoryName() : null)
                .name(entity.getName())
                .address(entity.getAddress())
                .lng(entity.getLng())
                .lat(entity.getLat())
                .isactive(entity.getIsactive())
                .geofences(geofenceDtos)
                .build();
    }
}
