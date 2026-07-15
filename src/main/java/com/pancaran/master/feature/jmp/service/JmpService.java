package com.pancaran.master.feature.jmp.service;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.feature.jmp.dto.JmpRequestDto;
import com.pancaran.master.feature.jmp.dto.JmpTripPlanRequestDto;
import com.pancaran.master.feature.jmp.dto.JmpResponseDto;
import com.pancaran.master.common.ApiException;
import org.springframework.data.domain.Page;
import com.apik.core.data.dto.SearchInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancaran.master.feature.jmp.entity.*;
import com.pancaran.master.feature.jmp.repository.JmpRepository;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;
import com.pancaran.master.feature.tripplan.service.PlaningService;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityCostEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityLeadTimeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "jmp-dbTransactionManager")
public class JmpService {

    private final JmpRepository repository;
    private final RouteRepository routeRepository;
    private final PlaningService planingService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JmpEntity saveJmp(JmpRequestDto dto) {
        boolean isJmpNew = dto.getId() == null || dto.getId().trim().isEmpty();
        JmpEntity jmp = new JmpEntity();
        jmp.setId(isJmpNew ? CoreUtil.createUUID() : dto.getId());
        jmp.setCustomerId(dto.getCustomerId());
        jmp.setConsigneeId(dto.getConsigneeId());
        jmp.setCommercialRoute(dto.getCommercialRoute());
        jmp.setReferenceNo(dto.getReferenceNo());
        jmp.setTitle(dto.getTitle());
        jmp.setDescription(dto.getDescription());
        jmp.setStatus(dto.getStatus());
        jmp.setCreatedBy("SYSTEM");
        jmp.setIsNotificationGlobal(dto.getIsNotificationGlobal() != null ? dto.getIsNotificationGlobal() : false);
 
        repository.saveJmp(jmp, isJmpNew);
 
        // Save JMP level units
        if (dto.getUnits() != null) {
            for (JmpTripPlanRequestDto.UnitRequestDto uDto : dto.getUnits()) {
                boolean isUnitNew = uDto.getId() == null || uDto.getId().trim().isEmpty();
                JmpUnitEntity u = new JmpUnitEntity();
                u.setId(isUnitNew ? CoreUtil.createUUID() : uDto.getId());
                u.setJmpId(jmp.getId());
                u.setUnitTypeId(uDto.getUnitTypeId());
                repository.saveUnit(u, isUnitNew);
            }
        }

        // Pre-fetch caches for master route propagation (Resolving N+1 issues)
        Map<String, ActivityEntity> masterActivityMap = new HashMap<>();
        Map<String, ActivityCostEntity> costMap = new HashMap<>();
        Map<String, ActivityLeadTimeEntity> leadTimeMap = new HashMap<>();

        if (Boolean.TRUE.equals(dto.getSaveToMaster())) {
            // 1. Fetch all master activities in a single query
            List<ActivityEntity> masterActivities = repository.findAllActivities();
            for (ActivityEntity act : masterActivities) {
                if (act.getName() != null) {
                    masterActivityMap.put(act.getName().trim().toUpperCase(), act);
                }
            }

            // 2. Identify all non-custom master route points being sent in JMP
            List<String> existingRoutePointIds = new ArrayList<>();
            if (dto.getTripPlans() != null) {
                for (JmpTripPlanRequestDto tpDto : dto.getTripPlans()) {
                    if (tpDto.getRoutePoints() != null) {
                        for (JmpTripPlanRequestDto.RoutePointRequestDto rpDto : tpDto.getRoutePoints()) {
                            if (rpDto.getRoutePointId() != null && !rpDto.getRoutePointId().trim().isEmpty() && !Boolean.TRUE.equals(rpDto.getIsCustom())) {
                                existingRoutePointIds.add(rpDto.getRoutePointId());
                            }
                        }
                    }
                }
            }

            // 3. Batch load all activity costs & lead times for these points in exactly 2 queries
            if (!existingRoutePointIds.isEmpty()) {
                List<ActivityCostEntity> preFetchedCosts = repository.findActivityCostsByRoutePoints(existingRoutePointIds);
                for (ActivityCostEntity c : preFetchedCosts) {
                    costMap.put(c.getRoutePointId() + "-" + c.getActivityId(), c);
                }

                List<ActivityLeadTimeEntity> preFetchedLeadTimes = repository.findActivityLeadTimesByRoutePoints(existingRoutePointIds);
                for (ActivityLeadTimeEntity l : preFetchedLeadTimes) {
                    leadTimeMap.put(l.getRoutePointId() + "-" + l.getActivityId(), l);
                }
            }
        }

        if (dto.getTripPlans() != null) {
            for (JmpTripPlanRequestDto tpDto : dto.getTripPlans()) {
                boolean isTpNew = tpDto.getId() == null || tpDto.getId().trim().isEmpty();
                JmpTripPlanEntity tp = new JmpTripPlanEntity();
                tp.setId(isTpNew ? CoreUtil.createUUID() : tpDto.getId());
                tp.setJmpId(jmp.getId());
                tp.setRouteId(tpDto.getRouteId());
                tp.setSeqno(tpDto.getSeqno());
                tp.setTransportMode(tpDto.getTransportMode());
                tp.setRemarks(tpDto.getRemarks());

                repository.saveTripPlan(tp, isTpNew);

                // Sea transit details
                if (tpDto.getSea() != null) {
                    boolean isSeaNew = tpDto.getSea().getId() == null || tpDto.getSea().getId().trim().isEmpty();
                    JmpSeaEntity sea = new JmpSeaEntity();
                    sea.setId(isSeaNew ? CoreUtil.createUUID() : tpDto.getSea().getId());
                    sea.setJmpTripPlanId(tp.getId());
                    sea.setOriginPortId(tpDto.getSea().getOriginPortId());
                    sea.setDestinationPortId(tpDto.getSea().getDestinationPortId());
                    sea.setVesselId(tpDto.getSea().getVesselId());
                    sea.setEtd(tpDto.getSea().getEtd());
                    sea.setEta(tpDto.getSea().getEta());
                    repository.saveSea(sea, isSeaNew);
                }

                // Air transit details
                if (tpDto.getAir() != null) {
                    boolean isAirNew = tpDto.getAir().getId() == null || tpDto.getAir().getId().trim().isEmpty();
                    JmpAirEntity air = new JmpAirEntity();
                    air.setId(isAirNew ? CoreUtil.createUUID() : tpDto.getAir().getId());
                    air.setJmpTripPlanId(tp.getId());
                    air.setOriginAirportId(tpDto.getAir().getOriginAirportId());
                    air.setDestinationAirportId(tpDto.getAir().getDestinationAirportId());
                    air.setAirline(tpDto.getAir().getAirline());
                    air.setFlightNo(tpDto.getAir().getFlightNo());
                    air.setEtd(tpDto.getAir().getEtd());
                    air.setEta(tpDto.getAir().getEta());
                    repository.saveAir(air, isAirNew);
                }

                // Drivers
                if (tpDto.getDrivers() != null) {
                    for (JmpTripPlanRequestDto.DriverRequestDto dDto : tpDto.getDrivers()) {
                        boolean isDriverNew = dDto.getId() == null || dDto.getId().trim().isEmpty();
                        JmpDriverEntity d = new JmpDriverEntity();
                        d.setId(isDriverNew ? CoreUtil.createUUID() : dDto.getId());
                        d.setJmpTripPlanId(tp.getId());
                        d.setDriverId(dDto.getDriverId());
                        d.setSeqno(dDto.getSeqno());
                        repository.saveDriver(d, isDriverNew);
                    }
                }



                // Extra Costs
                if (tpDto.getExtraCosts() != null) {
                    for (JmpTripPlanRequestDto.ExtraCostRequestDto ecDto : tpDto.getExtraCosts()) {
                        boolean isEcNew = ecDto.getId() == null || ecDto.getId().trim().isEmpty();
                        JmpExtraCostEntity ec = new JmpExtraCostEntity();
                        ec.setId(isEcNew ? CoreUtil.createUUID() : ecDto.getId());
                        ec.setJmpTripPlanId(tp.getId());
                        ec.setName(ecDto.getName());
                        ec.setAmount(ecDto.getAmount());
                        ec.setRemarks(ecDto.getRemarks());
                        repository.saveExtraCost(ec, isEcNew);
                    }
                }

                // Route Points
                Map<Integer, String> routePointSeqToIdMap = new HashMap<>();
                if (tpDto.getRoutePoints() != null) {
                    for (JmpTripPlanRequestDto.RoutePointRequestDto rpDto : tpDto.getRoutePoints()) {
                        boolean isRpNew = rpDto.getId() == null || rpDto.getId().trim().isEmpty();
                        JmpRoutePointEntity rp = new JmpRoutePointEntity();
                        rp.setId(isRpNew ? CoreUtil.createUUID() : rpDto.getId());
                        rp.setJmpTripPlanId(tp.getId());
                        rp.setRoutePointId(rpDto.getRoutePointId());
                        rp.setPoiId(rpDto.getPoiId());
                        rp.setSeqno(rpDto.getSeqno());
                        rp.setAlias(rpDto.getAlias());
                        rp.setAddress(rpDto.getAddress());
                        rp.setIsCustom(rpDto.getIsCustom() != null ? rpDto.getIsCustom() : false);
 
                        if (rpDto.getPaths() != null) {
                            try {
                                rp.setPaths(OBJECT_MAPPER.writeValueAsString(rpDto.getPaths()));
                            } catch (Exception e) {
                                rp.setPaths(rpDto.getPaths().toString());
                            }
                        }
 
                        if (Boolean.TRUE.equals(dto.getSaveToMaster()) && tp.getRouteId() != null) {
                            propagateRoutePoint(tp.getRouteId(), rp);
                        }
 
                        repository.saveRoutePoint(rp, isRpNew);
                        routePointSeqToIdMap.put(rp.getSeqno(), rp.getId());
 
                        // Save transaction activities
                        if (rpDto.getActivities() != null) {
                            for (JmpTripPlanRequestDto.ActivityRequestDto actDto : rpDto.getActivities()) {
                                boolean isActNew = actDto.getId() == null || actDto.getId().trim().isEmpty();
                                JmpActivityEntity act = new JmpActivityEntity();
                                act.setId(isActNew ? CoreUtil.createUUID() : actDto.getId());
                                act.setJmpRoutePointId(rp.getId());
                                act.setActivityId(actDto.getActivityId());
                                act.setActivityName(actDto.getActivityName());
                                act.setLeadtime(actDto.getLeadtime());
                                act.setCost(actDto.getCost());
                                act.setSeqno(actDto.getSeqno());
                                act.setRemarks(actDto.getRemarks());
                                act.setIsNotification(actDto.getIsNotification() != null ? actDto.getIsNotification() : false);
                                act.setNotes(actDto.getNotes());
 
                                if (Boolean.TRUE.equals(dto.getSaveToMaster()) && tp.getRouteId() != null && rp.getRoutePointId() != null) {
                                    propagateActivity(rp.getRoutePointId(), act, masterActivityMap, costMap, leadTimeMap);
                                }
 
                                repository.saveActivity(act, isActNew);
                            }
                        }
                    }
                }
 
                // Save segment route details (routeDetails)
                if (tpDto.getRouteDetails() != null) {
                    for (JmpTripPlanRequestDto.RouteDetailRequestDto rdDto : tpDto.getRouteDetails()) {
                        boolean isSegNew = rdDto.getId() == null || rdDto.getId().trim().isEmpty();
                        JmpRouteSegmentEntity seg = new JmpRouteSegmentEntity();
                        seg.setId(isSegNew ? CoreUtil.createUUID() : rdDto.getId());
                        seg.setJmpTripPlanId(tp.getId());
                        seg.setSeqno(rdDto.getSeqNo());
                        seg.setRemarks(rdDto.getRemarks());
                        
                        String startRpId = routePointSeqToIdMap.get(rdDto.getStartSeqNo());
                        String endRpId = routePointSeqToIdMap.get(rdDto.getEndSeqNo());
                        seg.setStartRoutePointId(startRpId);
                        seg.setEndRoutePointId(endRpId);
 
                        repository.saveRouteSegment(seg, isSegNew);
 
                        List<JmpTripPlanRequestDto.UnitRequestDto> segmentUnitsToSave = null;
                        if (dto.getUnits() != null && !dto.getUnits().isEmpty()) {
                            segmentUnitsToSave = dto.getUnits();
                        } else if (rdDto.getUnits() != null && !rdDto.getUnits().isEmpty()) {
                            segmentUnitsToSave = rdDto.getUnits();
                        }
 
                        if (segmentUnitsToSave != null) {
                            for (JmpTripPlanRequestDto.UnitRequestDto uDto : segmentUnitsToSave) {
                                boolean isSegUnitNew = uDto.getId() == null || uDto.getId().trim().isEmpty();
                                JmpRouteSegmentUnitEntity su = new JmpRouteSegmentUnitEntity();
                                su.setId(isSegUnitNew ? CoreUtil.createUUID() : uDto.getId());
                                su.setJmpRouteSegmentId(seg.getId());
                                su.setUnitTypeId(uDto.getUnitTypeId());
                                repository.saveRouteSegmentUnit(su, isSegUnitNew);
                            }
                        }
                    }
                }
            }
        }

        repository.flush();
        return jmp;
    }

    private void propagateRoutePoint(String routeId, JmpRoutePointEntity rp) {
        String masterRoutePointId = rp.getRoutePointId();
        
        if (masterRoutePointId == null || masterRoutePointId.trim().isEmpty() || Boolean.TRUE.equals(rp.getIsCustom())) {
            RoutePointEntity masterRp = new RoutePointEntity();
            
            masterRoutePointId = CoreUtil.createUUID();
            masterRp.setId(masterRoutePointId);
            masterRp.setRouteId(routeId);
            masterRp.setPoiId(rp.getPoiId());
            masterRp.setSeqno(rp.getSeqno());
            masterRp.setAlias(rp.getAlias());
            masterRp.setAddress(rp.getAddress());
            masterRp.setPaths(rp.getPaths());
            masterRp.setIszone(false);
            
            repository.saveMasterRoutePoint(masterRp);
            rp.setRoutePointId(masterRoutePointId); 
        } else {
            // Update existing master point's sequence if it has been shifted
            RoutePointEntity masterRp = repository.findRoutePointById(masterRoutePointId);
            if (masterRp != null) {
                masterRp.setSeqno(rp.getSeqno());
                masterRp.setAlias(rp.getAlias());
                masterRp.setAddress(rp.getAddress());
                masterRp.setPaths(rp.getPaths());
                repository.saveMasterRoutePoint(masterRp);
            }
        }
    }

    private void propagateActivity(
            String masterRoutePointId, 
            JmpActivityEntity act, 
            Map<String, ActivityEntity> masterActivityMap,
            Map<String, ActivityCostEntity> costMap,
            Map<String, ActivityLeadTimeEntity> leadTimeMap) {
        
        String activityId = act.getActivityId();
        
        // 1. Resolve activityId in master
        if (activityId == null || activityId.trim().isEmpty()) {
            String actNameKey = act.getActivityName() != null ? act.getActivityName().trim().toUpperCase() : "";
            ActivityEntity masterAct = masterActivityMap.get(actNameKey);
            if (masterAct == null) {
                masterAct = new ActivityEntity();
                activityId = CoreUtil.createUUID();
                masterAct.setId(activityId);
                masterAct.setName(act.getActivityName());
                masterAct.setCost(act.getCost());
                masterAct.setLeadTime(act.getLeadtime());
                
                repository.saveMasterActivity(masterAct);
                masterActivityMap.put(actNameKey, masterAct); // Cache it
            } else {
                activityId = masterAct.getId();
            }
            act.setActivityId(activityId);
        }

        // 2. Propagate to m_activity_cost
        if (act.getCost() != null) {
            String key = masterRoutePointId + "-" + activityId;
            ActivityCostEntity cost = costMap.get(key);
            if (cost == null) {
                cost = new ActivityCostEntity();
                cost.setId(CoreUtil.createUUID());
                cost.setRoutePointId(masterRoutePointId);
                cost.setActivityId(activityId);
                
                costMap.put(key, cost); // Cache it
            }
            cost.setAmount(act.getCost().doubleValue());
            repository.saveMasterActivityCost(cost);
        }

        // 3. Propagate to m_activity_lead_time
        if (act.getLeadtime() != null) {
            String key = masterRoutePointId + "-" + activityId;
            ActivityLeadTimeEntity leadTime = leadTimeMap.get(key);
            if (leadTime == null) {
                leadTime = new ActivityLeadTimeEntity();
                leadTime.setId(CoreUtil.createUUID());
                leadTime.setRoutePointId(masterRoutePointId);
                leadTime.setActivityId(activityId);
                
                leadTimeMap.put(key, leadTime); // Cache it
            }
            leadTime.setLeadtime(act.getLeadtime());
            repository.saveMasterActivityLeadTime(leadTime);
        }
    }

    @Transactional(value = "jmp-dbTransactionManager", readOnly = true)
    public Page<JmpEntity> getJmpPage(SearchInput input) {
        return repository.findJmpPage(input);
    }

    @Transactional(value = "jmp-dbTransactionManager", readOnly = true)
    public Page<JmpTripPlanEntity> getTripPlanPage(SearchInput input) {
        return repository.findTripPlanPage(input);
    }

    @Transactional(value = "jmp-dbTransactionManager", readOnly = true)
    public JmpResponseDto getJmpById(String id) {
        JmpEntity jmp = repository.findJmpById(id);
        if (jmp == null) {
            throw new ApiException(404, "Jmp not found with id: " + id);
        }

        // 1. Fetch units and trip plans
        List<JmpUnitEntity> units = repository.findUnitsByJmpId(id);
        List<JmpTripPlanEntity> tripPlans = repository.findTripPlansByJmpId(id);

        if (tripPlans.isEmpty()) {
            JmpResponseDto response = new JmpResponseDto();
            response.setId(jmp.getId());
            response.setCustomerId(jmp.getCustomerId());
            response.setCustomer(jmp.getCustomer());
            response.setConsigneeId(jmp.getConsigneeId());
            response.setConsignee(jmp.getConsignee());
            response.setCommercialRoute(jmp.getCommercialRoute());
            response.setReferenceNo(jmp.getReferenceNo());
            response.setTitle(jmp.getTitle());
            response.setDescription(jmp.getDescription());
            response.setStatus(jmp.getStatus());
            response.setIsNotificationGlobal(jmp.getIsNotificationGlobal());
            response.setCreatedAt(jmp.getCreatedAt());
            response.setCreatedBy(jmp.getCreatedBy());
            response.setUpdatedAt(jmp.getUpdatedAt());
            response.setUpdatedBy(jmp.getUpdatedBy());
            response.setUnits(units);
            response.setTripPlans(Collections.emptyList());
            return response;
        }

        // 2. Fetch everything related to trip plans in batch
        List<String> tripPlanIds = tripPlans.stream().map(JmpTripPlanEntity::getId).collect(Collectors.toList());
        
        List<JmpSeaEntity> seas = repository.findSeaByTripPlanIds(tripPlanIds);
        List<JmpAirEntity> airs = repository.findAirByTripPlanIds(tripPlanIds);
        List<JmpDriverEntity> drivers = repository.findDriversByTripPlanIds(tripPlanIds);
        List<JmpExtraCostEntity> extraCosts = repository.findExtraCostsByTripPlanIds(tripPlanIds);
        List<JmpRoutePointEntity> routePoints = repository.findRoutePointsByTripPlanIds(tripPlanIds);
        List<JmpRouteSegmentEntity> routeSegments = repository.findRouteSegmentsByTripPlanIds(tripPlanIds);

        // 3. Fetch activities for route points
        List<JmpActivityEntity> activities = Collections.emptyList();
        if (!routePoints.isEmpty()) {
            List<String> routePointIds = routePoints.stream().map(JmpRoutePointEntity::getId).collect(Collectors.toList());
            activities = repository.findActivitiesByRoutePointIds(routePointIds);
        }

        // 4. Fetch units for route segments
        List<JmpRouteSegmentUnitEntity> segmentUnits = Collections.emptyList();
        if (!routeSegments.isEmpty()) {
            List<String> routeSegmentIds = routeSegments.stream().map(JmpRouteSegmentEntity::getId).collect(Collectors.toList());
            segmentUnits = repository.findSegmentUnitsBySegmentIds(routeSegmentIds);
        }

        // 4.1 Batch fetch POIs referenced by route points to get their coordinates
        List<String> poiIds = new ArrayList<>();
        for (JmpRoutePointEntity rp : routePoints) {
            if (rp.getPoiId() != null && !rp.getPoiId().trim().isEmpty()) {
                poiIds.add(rp.getPoiId());
            }
        }
        for (JmpTripPlanEntity tp : tripPlans) {
            if (tp.getRouteId() != null) {
                List<com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity> mRps = routeRepository.findRoutePointsByRouteId(tp.getRouteId());
                for (com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity mrp : mRps) {
                    if (mrp.getPoiId() != null && !mrp.getPoiId().trim().isEmpty()) {
                        poiIds.add(mrp.getPoiId());
                    }
                }
            }
        }
        poiIds = poiIds.stream().distinct().collect(Collectors.toList());
        List<PoiEntity> pois = routeRepository.findPoisByIdsNative(poiIds);
        Map<String, PoiEntity> poiMap = pois.stream().collect(Collectors.toMap(PoiEntity::getId, p -> p, (p1, p2) -> p1));

        // 5. Build lookup maps for fast mapping in memory
        Map<String, JmpSeaEntity> seaMap = seas.stream()
                .collect(Collectors.toMap(JmpSeaEntity::getJmpTripPlanId, s -> s, (s1, s2) -> s1));
                
        Map<String, JmpAirEntity> airMap = airs.stream()
                .collect(Collectors.toMap(JmpAirEntity::getJmpTripPlanId, a -> a, (a1, a2) -> a1));
                
        Map<String, List<JmpDriverEntity>> driversMap = drivers.stream()
                .collect(Collectors.groupingBy(JmpDriverEntity::getJmpTripPlanId));
                
        Map<String, List<JmpExtraCostEntity>> extraCostsMap = extraCosts.stream()
                .collect(Collectors.groupingBy(JmpExtraCostEntity::getJmpTripPlanId));
                
        Map<String, List<JmpRoutePointEntity>> routePointsMap = routePoints.stream()
                .collect(Collectors.groupingBy(JmpRoutePointEntity::getJmpTripPlanId));
                
        Map<String, List<JmpRouteSegmentEntity>> routeSegmentsMap = routeSegments.stream()
                .collect(Collectors.groupingBy(JmpRouteSegmentEntity::getJmpTripPlanId));

        Map<String, List<JmpActivityEntity>> activitiesMap = activities.stream()
                .collect(Collectors.groupingBy(JmpActivityEntity::getJmpRoutePointId));

        Map<String, List<JmpRouteSegmentUnitEntity>> segmentUnitsMap = segmentUnits.stream()
                .collect(Collectors.groupingBy(JmpRouteSegmentUnitEntity::getJmpRouteSegmentId));

        // 6. Map to DTOs
        JmpResponseDto response = new JmpResponseDto();
        response.setId(jmp.getId());
        response.setCustomerId(jmp.getCustomerId());
        response.setCustomer(jmp.getCustomer());
        response.setConsigneeId(jmp.getConsigneeId());
        response.setConsignee(jmp.getConsignee());
        response.setCommercialRoute(jmp.getCommercialRoute());
        response.setReferenceNo(jmp.getReferenceNo());
        response.setTitle(jmp.getTitle());
        response.setDescription(jmp.getDescription());
        response.setStatus(jmp.getStatus());
        response.setIsNotificationGlobal(jmp.getIsNotificationGlobal());
        response.setCreatedAt(jmp.getCreatedAt());
        response.setCreatedBy(jmp.getCreatedBy());
        response.setUpdatedAt(jmp.getUpdatedAt());
        response.setUpdatedBy(jmp.getUpdatedBy());
        response.setUnits(units);

        List<JmpResponseDto.TripPlanResponseDto> tripPlanDtos = tripPlans.stream().map(tp -> {
            JmpResponseDto.TripPlanResponseDto tpDto = new JmpResponseDto.TripPlanResponseDto();
            tpDto.setId(tp.getId());
            tpDto.setJmpId(tp.getJmpId());
            tpDto.setRouteId(tp.getRouteId());
            tpDto.setSeqno(tp.getSeqno());
            tpDto.setTransportMode(tp.getTransportMode());
            tpDto.setRemarks(tp.getRemarks());
            tpDto.setCreatedAt(tp.getCreatedAt());

            tpDto.setSea(seaMap.get(tp.getId()));
            tpDto.setAir(airMap.get(tp.getId()));
            tpDto.setDrivers(driversMap.getOrDefault(tp.getId(), Collections.emptyList()));
            tpDto.setExtraCosts(extraCostsMap.getOrDefault(tp.getId(), Collections.emptyList()));

            // Map route points
            List<JmpRoutePointEntity> points = routePointsMap.getOrDefault(tp.getId(), Collections.emptyList());
            
            // 1. Fetch master route points if routeId is set
            List<RoutePointEntity> masterPoints = Collections.emptyList();
            if (tp.getRouteId() != null) {
                masterPoints = routeRepository.findRoutePointsByRouteId(tp.getRouteId());
            }

            // 2. Fetch master activities details if we have master points
            List<String> masterPointIds = masterPoints.stream().map(RoutePointEntity::getId).collect(Collectors.toList());
            List<ActivityLeadTimeEntity> mLeadTimes = Collections.emptyList();
            List<ActivityCostEntity> mActivityCosts = Collections.emptyList();
            if (!masterPointIds.isEmpty()) {
                mLeadTimes = routeRepository.findLeadTimesByRoutePointIds(masterPointIds);
                mActivityCosts = routeRepository.findActivityCostsByRoutePointIds(masterPointIds);
            }
            Map<String, List<ActivityLeadTimeEntity>> mLeadTimesMap = mLeadTimes.stream()
                    .collect(Collectors.groupingBy(ActivityLeadTimeEntity::getRoutePointId));
            Map<String, List<ActivityCostEntity>> mActivityCostsMap = mActivityCosts.stream()
                    .collect(Collectors.groupingBy(ActivityCostEntity::getRoutePointId));

            List<String> mActIds = new ArrayList<>();
            mLeadTimes.forEach(lt -> { if (lt.getActivityId() != null) mActIds.add(lt.getActivityId()); });
            mActivityCosts.forEach(ac -> { if (ac.getActivityId() != null) mActIds.add(ac.getActivityId()); });
            List<ActivityEntity> mActivities = Collections.emptyList();
            if (!mActIds.isEmpty()) {
                mActivities = routeRepository.findActivitiesByIds(mActIds.stream().distinct().collect(Collectors.toList()));
            }
            Map<String, ActivityEntity> mActivitiesMap = mActivities.stream()
                    .collect(Collectors.toMap(ActivityEntity::getId, java.util.function.Function.identity(), (a1, a2) -> a1));

            List<JmpResponseDto.RoutePointResponseDto> pointDtos = new ArrayList<>();

            // A. Process Master Points (overridden or default fallback)
            for (RoutePointEntity mp : masterPoints) {
                JmpRoutePointEntity txRp = points.stream()
                        .filter(p -> mp.getId().equals(p.getRoutePointId()))
                        .findFirst().orElse(null);

                JmpResponseDto.RoutePointResponseDto rpDto = new JmpResponseDto.RoutePointResponseDto();
                if (txRp != null) {
                    // Overridden: Use transaction data
                    rpDto.setId(txRp.getId());
                    rpDto.setJmpTripPlanId(txRp.getJmpTripPlanId());
                    rpDto.setRoutePointId(txRp.getRoutePointId());
                    rpDto.setPoiId(txRp.getPoiId());
                    rpDto.setSeqno(txRp.getSeqno());
                    rpDto.setAlias(txRp.getAlias());
                    rpDto.setAddress(txRp.getAddress());
                    rpDto.setIsCustom(txRp.getIsCustom());
                    rpDto.setSourceType("OVERRIDDEN");

                    if (txRp.getPaths() != null && !txRp.getPaths().trim().isEmpty()) {
                        try {
                            rpDto.setPaths(OBJECT_MAPPER.readValue(txRp.getPaths(), Object.class));
                        } catch (Exception e) {
                            rpDto.setPaths(txRp.getPaths());
                        }
                    }
                    rpDto.setActivities(activitiesMap.getOrDefault(txRp.getId(), Collections.emptyList()));
                } else {
                    // Default template point: Use master data
                    rpDto.setId("master-" + mp.getId());
                    rpDto.setJmpTripPlanId(tp.getId());
                    rpDto.setRoutePointId(mp.getId());
                    rpDto.setPoiId(mp.getPoiId());
                    rpDto.setSeqno(mp.getSeqno());
                    rpDto.setAlias(mp.getAlias());
                    rpDto.setAddress(mp.getAddress());
                    rpDto.setIsCustom(false);
                    rpDto.setSourceType("TEMPLATE");

                    if (mp.getPaths() != null && !mp.getPaths().trim().isEmpty()) {
                        try {
                            rpDto.setPaths(OBJECT_MAPPER.readValue(mp.getPaths(), Object.class));
                        } catch (Exception e) {
                            rpDto.setPaths(mp.getPaths());
                        }
                    }

                    // Map master activities to transient entities
                    List<ActivityLeadTimeEntity> lts = mLeadTimesMap.getOrDefault(mp.getId(), Collections.emptyList());
                    List<ActivityCostEntity> acs = mActivityCostsMap.getOrDefault(mp.getId(), Collections.emptyList());
                    Set<String> actIdsForPoint = new LinkedHashSet<>();
                    lts.forEach(lt -> actIdsForPoint.add(lt.getActivityId()));
                    acs.forEach(ac -> actIdsForPoint.add(ac.getActivityId()));

                    List<JmpActivityEntity> mockActs = actIdsForPoint.stream().map(actId -> {
                        ActivityEntity actDetail = mActivitiesMap.get(actId);
                        JmpActivityEntity mockAct = new JmpActivityEntity();
                        mockAct.setId("master-act-" + actId);
                        mockAct.setJmpRoutePointId("master-" + mp.getId());
                        mockAct.setActivityId(actId);
                        mockAct.setActivityName(actDetail != null ? actDetail.getName() : "");
                        mockAct.setLeadtime(lts.stream().filter(lt -> actId.equals(lt.getActivityId())).map(ActivityLeadTimeEntity::getLeadtime).findFirst().orElse(null));
                        mockAct.setCost(acs.stream().filter(ac -> actId.equals(ac.getActivityId())).map(ac -> java.math.BigDecimal.valueOf(ac.getAmount())).findFirst().orElse(null));
                        mockAct.setIsNotification(false);
                        return mockAct;
                    }).collect(Collectors.toList());
                    rpDto.setActivities(mockActs);
                }

                // Enrich route point with proximity hazards
                PoiEntity poi = poiMap.get(rpDto.getPoiId());
                if (poi != null && poi.getLat() != null && poi.getLng() != null) {
                    rpDto.setHazards(routeRepository.findHazardsByPoi(poi.getLat(), poi.getLng(), 50.0));
                } else {
                    rpDto.setHazards(Collections.emptyList());
                }

                pointDtos.add(rpDto);
            }

            // B. Process Custom/Additional Points from transaction (not matching any master point)
            for (JmpRoutePointEntity txRp : points) {
                if (txRp.getRoutePointId() == null || !masterPointIds.contains(txRp.getRoutePointId())) {
                    JmpResponseDto.RoutePointResponseDto rpDto = new JmpResponseDto.RoutePointResponseDto();
                    rpDto.setId(txRp.getId());
                    rpDto.setJmpTripPlanId(txRp.getJmpTripPlanId());
                    rpDto.setRoutePointId(txRp.getRoutePointId());
                    rpDto.setPoiId(txRp.getPoiId());
                    rpDto.setSeqno(txRp.getSeqno());
                    rpDto.setAlias(txRp.getAlias());
                    rpDto.setAddress(txRp.getAddress());
                    rpDto.setIsCustom(txRp.getIsCustom());
                    rpDto.setSourceType("CUSTOM");

                    if (txRp.getPaths() != null && !txRp.getPaths().trim().isEmpty()) {
                        try {
                            rpDto.setPaths(OBJECT_MAPPER.readValue(txRp.getPaths(), Object.class));
                        } catch (Exception e) {
                            rpDto.setPaths(txRp.getPaths());
                        }
                    }
                    rpDto.setActivities(activitiesMap.getOrDefault(txRp.getId(), Collections.emptyList()));

                    PoiEntity poi = poiMap.get(rpDto.getPoiId());
                    if (poi != null && poi.getLat() != null && poi.getLng() != null) {
                        rpDto.setHazards(routeRepository.findHazardsByPoi(poi.getLat(), poi.getLng(), 50.0));
                    } else {
                        rpDto.setHazards(Collections.emptyList());
                    }

                    pointDtos.add(rpDto);
                }
            }

            // C. Sort merged points by sequence number
            pointDtos.sort(java.util.Comparator.comparing(
                    JmpResponseDto.RoutePointResponseDto::getSeqno, 
                    java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())));

            tpDto.setRoutePoints(pointDtos);
 
            // Map route details (segments)
            List<JmpRouteSegmentEntity> segments = routeSegmentsMap.getOrDefault(tp.getId(), Collections.emptyList());
            List<JmpResponseDto.RouteDetailResponseDto> segmentDtos = segments.stream().map(seg -> {
                JmpResponseDto.RouteDetailResponseDto rdDto = new JmpResponseDto.RouteDetailResponseDto();
                rdDto.setId(seg.getId());
                rdDto.setJmpTripPlanId(seg.getJmpTripPlanId());
                rdDto.setStartRoutePointId(seg.getStartRoutePointId());
                rdDto.setEndRoutePointId(seg.getEndRoutePointId());
                rdDto.setSeqno(seg.getSeqno());
                rdDto.setRemarks(seg.getRemarks());
                rdDto.setUnits(segmentUnitsMap.getOrDefault(seg.getId(), Collections.emptyList()));

                // Enrich segment with hazards along the road polyline path from the merged pointDtos
                JmpResponseDto.RoutePointResponseDto endPoint = pointDtos.stream()
                        .filter(p -> p.getId().equals(seg.getEndRoutePointId()))
                        .findFirst().orElse(null);

                if (endPoint != null && endPoint.getPaths() != null) {
                    String lineString = planingService.convertPathsToLineString(endPoint.getPaths());
                    if (lineString != null) {
                        rdDto.setHazards(routeRepository.findHazardsByLineString(lineString));
                    } else {
                        rdDto.setHazards(Collections.emptyList());
                    }
                } else {
                    rdDto.setHazards(Collections.emptyList());
                }

                return rdDto;
            }).collect(Collectors.toList());
            tpDto.setRouteDetails(segmentDtos);

            return tpDto;
        }).collect(Collectors.toList());

        response.setTripPlans(tripPlanDtos);
        return response;
    }
}
