package com.pancaran.master.feature.tripplan.service.processor;

import com.pancaran.master.common.ApiException;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaningProcessor {

    private final RouteRepository routeRepository;

    public void process(RouteAggregate aggregate) {
        List<RoutePointEntity> points = aggregate.getRoutePoints();
        if (points == null || points.size() < 2) {
            throw new ApiException(400, "Route must have at least 2 points (Start and End).");
        }

        for (int i = 0; i < points.size(); i++) {
            RoutePointEntity point = points.get(i);
            if (point.getSeqno() == null) {
                throw new ApiException(400, "Route point sequence number cannot be null.");
            }
        }
    }
}
