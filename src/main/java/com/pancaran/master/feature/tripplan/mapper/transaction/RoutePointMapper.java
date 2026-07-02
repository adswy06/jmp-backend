package com.pancaran.master.feature.tripplan.mapper.transaction;

import com.apik.core.common.helper.CoreUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointCreateDto;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoutePointMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public List<RoutePointEntity> toEntities(
            RouteCreateRequestDto dto,
            RouteEntity route) {

        List<RoutePointEntity> result = new ArrayList<>();

        for (RoutePointCreateDto item : dto.getRoutePoints()) {

            RoutePointEntity entity = new RoutePointEntity();

            entity.setId(CoreUtil.createUUID());
            entity.setRouteId(route.getId());
            entity.setPoiId(item.getPoiId());
            entity.setSeqno(item.getSeqNo());
            entity.setAlias(item.getAlias());
            entity.setAddress(item.getAddress());

            if (item.getPaths() != null) {
                try {
                    entity.setPaths(OBJECT_MAPPER.writeValueAsString(item.getPaths()));
                } catch (Exception e) {
                    entity.setPaths(item.getPaths().toString());
                }
            }

            result.add(entity);

        }

        return result;

    }
}
