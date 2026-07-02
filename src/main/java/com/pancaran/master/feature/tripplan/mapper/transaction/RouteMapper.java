package com.pancaran.master.feature.tripplan.mapper.transaction;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteEntity;
import org.springframework.stereotype.Component;

@Component
public class RouteMapper {
    public RouteEntity toEntity(RouteCreateRequestDto dto) {

        RouteEntity entity = new RouteEntity();

        entity.setId(CoreUtil.createUUID());
        entity.setAlias(dto.getAlias());
        entity.setName(dto.getName());
        entity.setDistanceKm(dto.getDistanceKm());
        entity.setJourneyLeadTime(dto.getJourneyLeadTime());
        entity.setBasicCost(dto.getBasicCost());

        return entity;
    }
}
