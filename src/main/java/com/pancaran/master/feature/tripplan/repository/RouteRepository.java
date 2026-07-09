package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.apik.core.data.dto.SearchInput;
import java.util.*;

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

    public Page<RouteEntity> findRoutePage(SearchInput input) {
        StringBuilder selectJpql = new StringBuilder("select r from RouteEntity r where r.deleted = false");
        StringBuilder countJpql = new StringBuilder("select count(r) from RouteEntity r where r.deleted = false");
        
        Map<String, Object> parameters = new HashMap<>();
        
        Long totalRecords = entityManager.createQuery("select count(r) from RouteEntity r where r.deleted = false", Long.class)
                .getSingleResult();

        StringBuilder filterConditions = new StringBuilder();
        
        String search = input.getSearch();
        if (search != null) {
            search = search.replace("%", "").trim();
        }
        if (search != null && !search.isEmpty()) {
            String searchVal = "%" + search.toUpperCase() + "%";
            filterConditions.append(" and (upper(r.name) like :search or upper(r.alias) like :search)");
            parameters.put("search", searchVal);
        }

        Map<?, ?> filters = input.getFilter();
        if (filters != null && !filters.isEmpty()) {
            int i = 0;
            for (Map.Entry<?, ?> entry : filters.entrySet()) {
                String fieldName = entry.getKey() != null ? entry.getKey().toString() : "";
                Object valObj = entry.getValue();
                if (valObj == null) {
                    continue;
                }
                String filterValue = valObj.toString().trim();
                if (filterValue.isEmpty()) {
                    continue;
                }
                String paramName = "filter_" + i;

                if ("name".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and upper(r.name) like :").append(paramName);
                    parameters.put(paramName, "%" + filterValue.toUpperCase() + "%");
                } else if ("alias".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and upper(r.alias) = :").append(paramName);
                    parameters.put(paramName, filterValue.toUpperCase());
                } else {
                    filterConditions.append(" and upper(r.").append(fieldName).append(") like :").append(paramName);
                    parameters.put(paramName, "%" + filterValue.toUpperCase() + "%");
                }
                i++;
            }
        }

        selectJpql.append(filterConditions);
        countJpql.append(filterConditions);

        Pageable pageable = input.getPageable() != null ? input.getPageable() : PageRequest.of(0, 20);
        Sort sort = pageable.getSort();

        if (sort != null && sort.isSorted()) {
            StringBuilder orderClause = new StringBuilder(" order by");
            boolean first = true;
            for (Sort.Order order : sort) {
                String property = order.getProperty();
                String dir = order.getDirection().isAscending() ? "asc" : "desc";
                if (Arrays.asList("name", "alias", "distanceKm", "journeyLeadTime", "basicCost", "createdAt").contains(property)) {
                    if (!first) orderClause.append(",");
                    orderClause.append(" r.").append(property).append(" ").append(dir);
                    first = false;
                }
            }
            if (!first) {
                selectJpql.append(orderClause);
            } else {
                selectJpql.append(" order by r.createdAt desc");
            }
        } else {
            selectJpql.append(" order by r.createdAt desc");
        }

        var query = entityManager.createQuery(selectJpql.toString(), RouteEntity.class);
        parameters.forEach(query::setParameter);

        int start = (int) pageable.getOffset();
        int length = pageable.getPageSize();
        
        query.setFirstResult(start);
        query.setMaxResults(length);

        List<RouteEntity> list = query.getResultList();

        Long filteredRecords;
        if (parameters.isEmpty()) {
            filteredRecords = totalRecords;
        } else {
            var countQuery = entityManager.createQuery(countJpql.toString(), Long.class);
            parameters.forEach(countQuery::setParameter);
            filteredRecords = countQuery.getSingleResult();
        }

        return new PageImpl<>(list, pageable, filteredRecords);
    }
}
