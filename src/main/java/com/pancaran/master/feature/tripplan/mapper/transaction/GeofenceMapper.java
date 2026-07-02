package com.pancaran.master.feature.tripplan.mapper.transaction;

import com.apik.core.common.helper.CoreUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointCreateDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointGeofenceDto;
import com.pancaran.master.feature.tripplan.entity.transaction.GeofenceEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GeofenceMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<GeofenceEntity> toEntity(
            List<RoutePointEntity> routePoints,
            RouteCreateRequestDto dto) {

        List<GeofenceEntity> result = new ArrayList<>();

        Map<Integer, RoutePointEntity> map =
                routePoints.stream()
                        .collect(Collectors.toMap(
                                RoutePointEntity::getSeqno,
                                Function.identity()));

        for (RoutePointCreateDto point : dto.getRoutePoints()) {

            if (point.getGeofence() == null)
                continue;

            RoutePointEntity rp = map.get(point.getSeqNo());

            for (RoutePointGeofenceDto gf : point.getGeofence()) {

                GeofenceEntity entity = new GeofenceEntity();

                entity.setId(CoreUtil.createUUID());

                entity.setPoiId(rp != null ? rp.getPoiId() : null);
                entity.setRouteId(rp != null ? rp.getRouteId() : null);

                entity.setShapeType(gf.getShapeType());

                entity.setRadius(gf.getRadius());

                if (gf.getPaths() != null) {
                    try {
                        entity.setPaths(OBJECT_MAPPER.writeValueAsString(gf.getPaths()));
                    } catch (Exception e) {
                        entity.setPaths(gf.getPaths().toString());
                    }
                }

                result.add(entity);

            }

        }

        return result;

    }

}
