package com.pancaran.master.feature.jmp.service;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.feature.jmp.dto.JmpRequestDto;
import com.pancaran.master.feature.jmp.dto.JmpTripPlanRequestDto;
import org.springframework.data.domain.Page;
import com.apik.core.data.dto.SearchInput;
import com.pancaran.master.feature.jmp.entity.*;
import com.pancaran.master.feature.jmp.repository.JmpRepository;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
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
    private static final com.fasterxml.jackson.databind.ObjectMapper OBJECT_MAPPER = new com.fasterxml.jackson.databind.ObjectMapper();

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
}
