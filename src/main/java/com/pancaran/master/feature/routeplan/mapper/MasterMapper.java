package com.pancaran.master.feature.routeplan.mapper;

import com.pancaran.master.feature.routeplan.dto.RouteHeaderDto;
import com.pancaran.master.feature.routeplan.entity.master.MstRouteEntity;
import org.springframework.stereotype.Component;

@Component
public class MasterMapper {
    public RouteHeaderDto toDto(MstRouteEntity entity) {

        if (entity == null) {
            return null;
        }

        RouteHeaderDto dto = new RouteHeaderDto();

        dto.setId(entity.getId());
        dto.setRouteName(entity.getAlias());
        dto.setLocFrom(entity.getLocationFromId());
        dto.setLocTo(entity.getLocationToId());

        return dto;
    }
}
