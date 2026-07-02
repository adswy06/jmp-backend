package com.pancaran.master.feature.tripplan.mapper.transaction;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.RouteSegmentDto;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteSegmentEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteSegmentUnitEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RouteSegmentMapper {

    public List<RouteSegmentEntity> toSegment(
            RouteEntity route,
            List<RoutePointEntity> routePoints,
            RouteCreateRequestDto request) {

        Map<Integer, RoutePointEntity> pointMap = routePoints.stream()
                .collect(Collectors.toMap(RoutePointEntity::getSeqno, Function.identity()));

        List<RouteSegmentEntity> result = new ArrayList<>();

        if (request.getRouteDetails() == null) {
            return result;
        }

        for (RouteSegmentDto dto : request.getRouteDetails()) {
            RouteSegmentEntity entity = new RouteSegmentEntity();
            entity.setId(CoreUtil.createUUID());
            entity.setRouteId(route.getId());
            entity.setSeqno(dto.getSeqNo());
            entity.setRemarks(dto.getRemarks());

            RoutePointEntity startPoint = pointMap.get(dto.getStartSeqNo());
            RoutePointEntity endPoint = pointMap.get(dto.getEndSeqNo());

            entity.setStartRoutePointId(startPoint != null ? startPoint.getId() : null);
            entity.setEndRoutePointId(endPoint != null ? endPoint.getId() : null);

            result.add(entity);
        }

        return result;
    }

    public List<RouteSegmentUnitEntity> toUnit(
            List<RouteSegmentEntity> segments,
            RouteCreateRequestDto request) {

        Map<Integer, RouteSegmentEntity> segmentMap = segments.stream()
                .collect(Collectors.toMap(RouteSegmentEntity::getSeqno, Function.identity()));

        List<RouteSegmentUnitEntity> result = new ArrayList<>();

        if (request.getRouteDetails() == null) {
            return result;
        }

        for (RouteSegmentDto dto : request.getRouteDetails()) {
            RouteSegmentEntity segment = segmentMap.get(dto.getSeqNo());
            if (segment == null || dto.getUnits() == null) {
                continue;
            }

            for (RouteSegmentDto.RouteSegmentUnit unitDto : dto.getUnits()) {
                RouteSegmentUnitEntity entity = new RouteSegmentUnitEntity();
                entity.setId(CoreUtil.createUUID());
                entity.setRouteSegmentId(segment.getId());
                entity.setUnitTypeId(unitDto.getUnitTypeId());

                result.add(entity);
            }
        }

        return result;
    }
}
