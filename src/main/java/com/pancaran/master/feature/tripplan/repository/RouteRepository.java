package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RouteRepository {
    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    public boolean existsAlias(String alias){

        Long count = entityManager.createQuery("""
                select count(r)
                from RouteEntity r
                where upper(r.alias)=upper(:alias) and r.deleted = false
                """,Long.class)
                .setParameter("alias",alias)
                .getSingleResult();

        return count > 0;
    }

    public void save(RouteAggregate aggregate){

        if (aggregate.getRoute().getAlias() != null) {
            softDeleteRouteByAlias(aggregate.getRoute().getAlias());
        }

        saveZones(aggregate.getZones());

        savePois(aggregate.getPois());

        saveRoute(aggregate.getRoute());

        saveRoutePoints(aggregate.getRoutePoints());

        saveGeofence(aggregate.getGeofences());

        saveLeadTime(aggregate.getLeadTimes());

        saveActivityCost(aggregate.getActivityCosts());

        saveExtraCost(aggregate.getExtraCosts());

        saveRouteDetail(aggregate.getRouteDetails());

        saveRouteUnit(aggregate.getRouteUnits());

        entityManager.flush();

        entityManager.clear();
    }

    private void saveRoute(RouteEntity entity){

        entityManager.persist(entity);

    }

    private <T> void batchPersist(List<T> entities){

        if(entities==null || entities.isEmpty())
            return;

        int batchSize=100;

        int i=0;

        for(T entity : entities){

            entityManager.persist(entity);

            i++;

            if(i%batchSize==0){

                entityManager.flush();

                entityManager.clear();

            }

        }

    }

    private void saveRoutePoints(List<RoutePointEntity> list){

        batchPersist(list);

    }

    private void saveGeofence(List<GeofenceEntity> list){

        batchPersist(list);

    }

    private void saveLeadTime(List<ActivityLeadTimeEntity> list){

        batchPersist(list);

    }

    private void saveActivityCost(List<ActivityCostEntity> list){

        batchPersist(list);

    }

    private void saveExtraCost(List<ActivityExtraCostEntity> list){

        batchPersist(list);

    }

    private void saveRouteDetail(List<RouteSegmentEntity> list){

        batchPersist(list);

    }

    private void saveRouteUnit(List<RouteSegmentUnitEntity> list){

        batchPersist(list);

    }

    private void saveZones(List<ZoneEntity> list){
        if (list == null || list.isEmpty())
            return;

        List<String> ids = list.stream()
                .map(ZoneEntity::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        if (ids.isEmpty()) return;

        List<ZoneEntity> existingList = entityManager.createQuery(
                "select z from ZoneEntity z where z.id in :ids", ZoneEntity.class)
                .setParameter("ids", ids)
                .getResultList();

        java.util.Map<String, ZoneEntity> existingMap = existingList.stream()
                .collect(java.util.stream.Collectors.toMap(ZoneEntity::getId, java.util.function.Function.identity()));

        for (ZoneEntity entity : list) {
            ZoneEntity existing = existingMap.get(entity.getId());
            if (existing == null) {
                entityManager.persist(entity);
            } else {
                existing.setName(entity.getName());
                existing.setAddress(entity.getAddress());
                existing.setPaths(entity.getPaths());
            }
        }
    }

    private void savePois(List<PoiEntity> list){
        if (list == null || list.isEmpty())
            return;

        List<String> ids = list.stream()
                .map(PoiEntity::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        if (ids.isEmpty()) return;

        List<PoiEntity> existingList = entityManager.createQuery(
                "select p from PoiEntity p where p.id in :ids", PoiEntity.class)
                .setParameter("ids", ids)
                .getResultList();

        java.util.Map<String, PoiEntity> existingMap = existingList.stream()
                .collect(java.util.stream.Collectors.toMap(PoiEntity::getId, java.util.function.Function.identity()));

        for (PoiEntity entity : list) {
            PoiEntity existing = existingMap.get(entity.getId());
            if (existing == null) {
                entityManager.persist(entity);
            } else {
                existing.setName(entity.getName());
                existing.setAddress(entity.getAddress());
                existing.setLat(entity.getLat());
                existing.setLng(entity.getLng());
                existing.setZone(entity.getZone());
            }
        }
    }

    public List<PoiEntity> findPoisByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return entityManager.createQuery("""
                select p
                from PoiEntity p
                where p.id in :ids
                """, PoiEntity.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    private void softDeleteRouteByAlias(String alias) {
        entityManager.createQuery("""
                update RouteEntity r
                set r.active = false, r.deleted = true, r.deletedAt = :now
                where r.alias = :alias and r.deleted = false
                """)
                .setParameter("alias", alias)
                .setParameter("now", java.time.LocalDateTime.now())
                .executeUpdate();
    }
}
