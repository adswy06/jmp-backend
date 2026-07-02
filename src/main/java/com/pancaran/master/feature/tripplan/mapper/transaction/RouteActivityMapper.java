package com.pancaran.master.feature.tripplan.mapper.transaction;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.feature.tripplan.dto.request.RouteActivityDto;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.RoutePointCreateDto;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityCostEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityExtraCostEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityLeadTimeEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RouteActivityMapper {

    public List<ActivityLeadTimeEntity> toLeadTime(
            List<RoutePointEntity> routePoints,
            RouteCreateRequestDto request) {

        Map<Integer, RoutePointEntity> pointMap =
                routePoints.stream()
                        .collect(Collectors.toMap(
                                RoutePointEntity::getSeqno,
                                Function.identity()));

        List<ActivityLeadTimeEntity> result = new ArrayList<>();

        for (RoutePointCreateDto point : request.getRoutePoints()) {

            RoutePointEntity rp = pointMap.get(point.getSeqNo());

            if (point.getActivities() == null)
                continue;

            for (RouteActivityDto activity : point.getActivities()) {

                ActivityLeadTimeEntity entity =
                        new ActivityLeadTimeEntity();

                entity.setId(CoreUtil.createUUID());

                entity.setRoutePointId(rp != null ? rp.getId() : null);

                entity.setActivityId(activity.getActivityId());

                entity.setLeadtime(activity.getLeadTime());

                result.add(entity);
            }
        }

        return result;
    }

    public List<ActivityCostEntity> toCost(
            List<RoutePointEntity> routePoints,
            RouteCreateRequestDto request) {

        Map<Integer, RoutePointEntity> pointMap =
                routePoints.stream()
                        .collect(Collectors.toMap(
                                RoutePointEntity::getSeqno,
                                Function.identity()));

        List<ActivityCostEntity> result = new ArrayList<>();

        for (RoutePointCreateDto point : request.getRoutePoints()) {

            RoutePointEntity rp = pointMap.get(point.getSeqNo());

            if (point.getActivities() == null)
                continue;

            for (RouteActivityDto activity : point.getActivities()) {

                ActivityCostEntity entity =
                        new ActivityCostEntity();

                entity.setId(CoreUtil.createUUID());

                entity.setRoutePointId(rp != null ? rp.getId() : null);

                entity.setActivityId(activity.getActivityId());

                entity.setAmount(activity.getAmount());

                result.add(entity);
            }
        }

        return result;
    }

    public List<ActivityExtraCostEntity> toExtraCosts(
            List<RoutePointEntity> routePoints,
            RouteCreateRequestDto request) {

        Map<Integer, RoutePointEntity> pointMap =
                routePoints.stream()
                        .collect(Collectors.toMap(
                                RoutePointEntity::getSeqno,
                                Function.identity()));

        List<ActivityExtraCostEntity> result = new ArrayList<>();

        for (RoutePointCreateDto point : request.getRoutePoints()) {

            RoutePointEntity rp = pointMap.get(point.getSeqNo());

            if (point.getActivities() == null)
                continue;

            for (RouteActivityDto activity : point.getActivities()) {

                if (activity.getExtraCost() == null)
                    continue;

                for (RouteActivityDto.RouteActivityExtraCost extra : activity.getExtraCost()) {

                    ActivityExtraCostEntity entity =
                            new ActivityExtraCostEntity();

                    entity.setId(CoreUtil.createUUID());

                    entity.setRoutePointId(rp != null ? rp.getId() : null);

                    entity.setActivityId(activity.getActivityId());

                    entity.setName(extra.getName());

                    entity.setAmount(extra.getAmount());

                    result.add(entity);
                }
            }
        }

        return result;
    }
}
