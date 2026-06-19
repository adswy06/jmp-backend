package com.pancaran.master.feature.routeplan.service;

import com.apik.core.common.exceptions.BusinessException;
import com.pancaran.master.feature.routeplan.dto.RouteHeaderDto;
import com.pancaran.master.feature.routeplan.dto.request.RouteDetailRequest;
import com.pancaran.master.feature.routeplan.dto.request.RoutePlanRequest;
import com.pancaran.master.feature.routeplan.dto.request.RoutePlanSegmentRequest;
import com.pancaran.master.feature.routeplan.entity.master.MstRouteDetailEntity;
import com.pancaran.master.feature.routeplan.entity.master.MstRouteEntity;
import com.pancaran.master.feature.routeplan.entity.master.MstRouteSegmentEntity;
import com.pancaran.master.feature.routeplan.helper.GeometryUtil;
import com.pancaran.master.feature.routeplan.mapper.MasterMapper;
import com.pancaran.master.feature.routeplan.repository.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.apik.core.common.helper.CoreUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private MasterMapper masterMapper;

    @Transactional("jmp-dbTransactionManager")
    public String saveBaseRoute(RoutePlanRequest request) throws BusinessException {

        /*
         * Header route.
         * Satu route bisa memiliki banyak titik (detail)
         * dan setiap titik bisa memiliki banyak segment jalan.
         */
        MstRouteEntity route = new MstRouteEntity();

        route.setId(CoreUtil.createUUID());
        route.setAlias(request.getRouteName());
        route.setLocationFromId(request.getLocationFromId());
        route.setLocationToId(request.getLocationToId());
        route.setIsDelete(false);

        List<MstRouteDetailEntity> routeDetails = new ArrayList<>();

        /*
         * Mapping seluruh titik route.
         */
        if (request.getPoints() != null && !request.getPoints().isEmpty()) {

            for (RouteDetailRequest pointRequest : request.getPoints()) {
                MstRouteDetailEntity detail = new MstRouteDetailEntity();

                detail.setId(CoreUtil.createUUID());
                detail.setRoute(route);
                detail.setSequence((double) pointRequest.getSequence());
                detail.setAddress(pointRequest.getAddress());
                detail.setLat(pointRequest.getLat());
                detail.setLng(pointRequest.getLng());
                detail.setDistance(pointRequest.getDistance());
                detail.setGeom(
                        GeometryUtil.createPoint(
                                pointRequest.getLat(),
                                pointRequest.getLng()
                        )
                );

                List<MstRouteSegmentEntity> segments = new ArrayList<>();
                /*
                 * Segment adalah jalan yang dilalui
                 * dari titik sebelumnya menuju titik ini.
                 */
                if (pointRequest.getSegments() != null &&
                        !pointRequest.getSegments().isEmpty()) {

                    for (RoutePlanSegmentRequest segmentRequest : pointRequest.getSegments()) {

                        MstRouteSegmentEntity segment = new MstRouteSegmentEntity();

                        segment.setId(CoreUtil.createUUID());
                        segment.setRouteDetail(detail);
                        segment.setSequenceNo(segmentRequest.getSequence());
                        segment.setAddress(segmentRequest.getAddress());
                        segment.setDistance(segmentRequest.getDistance());
                        segment.setDuration(segmentRequest.getDuration());
                        segment.setInstructions(segmentRequest.getInstructions());
                        segment.setDescription(segmentRequest.getDescription());

                        /*
                         * Simpan polyline jalan yang dilewati
                         * ke kolom geography(LineString)
                         */
                        segment.setSegmentGeom(
                                GeometryUtil.createLineString(
                                        segmentRequest.getCoordinates()
                                )
                        );

                        segments.add(segment);
                    }
                }

                detail.setSegments(segments);
                routeDetails.add(detail);
            }
        }

        /*
         * Attach seluruh detail ke route.
         * Karena relasi menggunakan CascadeType.ALL,
         * cukup save route sekali saja.
         */
        route.setRouteDetails(routeDetails);

        routeRepository.save(route);

        return route.getId();
    }

    @Transactional("jmp-dbTransactionManager")
    public RouteHeaderDto findRouteHeaderById(String id){
        MstRouteEntity data = routeRepository.findById(id).orElse(null);
        return masterMapper.toDto(data);
    }
}
