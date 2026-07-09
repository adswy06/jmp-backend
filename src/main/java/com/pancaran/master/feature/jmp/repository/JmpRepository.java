package com.pancaran.master.feature.jmp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.apik.core.data.dto.SearchInput;
import com.pancaran.master.feature.jmp.entity.*;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityCostEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityLeadTimeEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JmpRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    public void saveJmp(JmpEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveTripPlan(JmpTripPlanEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpTripPlanEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveRoutePoint(JmpRoutePointEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpRoutePointEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveActivity(JmpActivityEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpActivityEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveExtraCost(JmpExtraCostEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpExtraCostEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveDriver(JmpDriverEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpDriverEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveUnit(JmpUnitEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpUnitEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
 
    public void saveRouteSegment(JmpRouteSegmentEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpRouteSegmentEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
 
    public void saveRouteSegmentUnit(JmpRouteSegmentUnitEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpRouteSegmentUnitEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveSea(JmpSeaEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpSeaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveAir(JmpAirEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
        } else if (entityManager.find(JmpAirEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    // Master Route Propagation Helper Methods
    public RoutePointEntity findRoutePointById(String id) {
        return entityManager.find(RoutePointEntity.class, id);
    }

    public List<ActivityEntity> findAllActivities() {
        return entityManager.createQuery(
                "select a from ActivityEntity a", ActivityEntity.class)
                .getResultList();
    }

    public List<ActivityCostEntity> findActivityCostsByRoutePoints(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        return entityManager.createQuery(
                "select c from ActivityCostEntity c where c.routePointId in :routePointIds",
                ActivityCostEntity.class)
                .setParameter("routePointIds", routePointIds)
                .getResultList();
    }

    public List<ActivityLeadTimeEntity> findActivityLeadTimesByRoutePoints(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        return entityManager.createQuery(
                "select l from ActivityLeadTimeEntity l where l.routePointId in :routePointIds",
                ActivityLeadTimeEntity.class)
                .setParameter("routePointIds", routePointIds)
                .getResultList();
    }

    public void saveMasterRoutePoint(RoutePointEntity entity) {
        if (entityManager.find(RoutePointEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveMasterActivity(ActivityEntity entity) {
        if (entityManager.find(ActivityEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveMasterActivityCost(ActivityCostEntity entity) {
        if (entityManager.find(ActivityCostEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void saveMasterActivityLeadTime(ActivityLeadTimeEntity entity) {
        if (entityManager.find(ActivityLeadTimeEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }

    public void flush() {
        entityManager.flush();
    }

    public void clear() {
        entityManager.clear();
    }

    public Page<JmpEntity> findJmpPage(SearchInput input) {
        StringBuilder selectJpql = new StringBuilder("select j from JmpEntity j where 1=1");
        StringBuilder countJpql = new StringBuilder("select count(j) from JmpEntity j where 1=1");
        
        Map<String, Object> parameters = new HashMap<>();
        
        Long totalRecords = entityManager.createQuery("select count(j) from JmpEntity j", Long.class)
                .getSingleResult();

        StringBuilder filterConditions = new StringBuilder();
        
        String search = input.getSearch();
        if (search != null) {
            search = search.replace("%", "").trim();
        }
        if (search != null && !search.isEmpty()) {
            String searchVal = "%" + search.toUpperCase() + "%";
            filterConditions.append(" and (upper(j.title) like :search or upper(j.description) like :search or upper(j.referenceNo) like :search or upper(j.commercialRoute) like :search or upper(j.status) like :search)");
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

                if ("status".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and upper(j.status) = :").append(paramName);
                    parameters.put(paramName, filterValue.toUpperCase());
                } else if ("customerId".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and j.customerId = :").append(paramName);
                    parameters.put(paramName, filterValue);
                } else if ("consigneeId".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and j.consigneeId = :").append(paramName);
                    parameters.put(paramName, filterValue);
                } else {
                    filterConditions.append(" and upper(j.").append(fieldName).append(") like :").append(paramName);
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
                if (Arrays.asList("title", "description", "status", "referenceNo", "commercialRoute", "createdAt").contains(property)) {
                    if (!first) orderClause.append(",");
                    orderClause.append(" j.").append(property).append(" ").append(dir);
                    first = false;
                }
            }
            if (!first) {
                selectJpql.append(orderClause);
            } else {
                selectJpql.append(" order by j.createdAt desc");
            }
        } else {
            selectJpql.append(" order by j.createdAt desc");
        }

        var query = entityManager.createQuery(selectJpql.toString(), JmpEntity.class);
        parameters.forEach(query::setParameter);

        int start = (int) pageable.getOffset();
        int length = pageable.getPageSize();
        
        query.setFirstResult(start);
        query.setMaxResults(length);

        List<JmpEntity> list = query.getResultList();
        for (JmpEntity jmp : list) {
            jmp.setCustomer(null);
            jmp.setConsignee(null);
        }

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

    public Page<JmpTripPlanEntity> findTripPlanPage(SearchInput input) {
        StringBuilder selectJpql = new StringBuilder("select tp from JmpTripPlanEntity tp where 1=1");
        StringBuilder countJpql = new StringBuilder("select count(tp) from JmpTripPlanEntity tp where 1=1");
        
        Map<String, Object> parameters = new HashMap<>();
        
        Long totalRecords = entityManager.createQuery("select count(tp) from JmpTripPlanEntity tp", Long.class)
                .getSingleResult();

        StringBuilder filterConditions = new StringBuilder();
        
        String search = input.getSearch();
        if (search != null) {
            search = search.replace("%", "").trim();
        }
        if (search != null && !search.isEmpty()) {
            String searchVal = "%" + search.toUpperCase() + "%";
            filterConditions.append(" and (upper(tp.transportMode) like :search or upper(tp.remarks) like :search)");
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

                if ("jmpId".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and tp.jmpId = :").append(paramName);
                    parameters.put(paramName, filterValue);
                } else if ("routeId".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and tp.routeId = :").append(paramName);
                    parameters.put(paramName, filterValue);
                } else if ("transportMode".equalsIgnoreCase(fieldName)) {
                    filterConditions.append(" and upper(tp.transportMode) = :").append(paramName);
                    parameters.put(paramName, filterValue.toUpperCase());
                } else {
                    filterConditions.append(" and upper(tp.").append(fieldName).append(") like :").append(paramName);
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
                if (Arrays.asList("transportMode", "remarks", "seqno").contains(property)) {
                    if (!first) orderClause.append(",");
                    orderClause.append(" tp.").append(property).append(" ").append(dir);
                    first = false;
                }
            }
            if (!first) {
                selectJpql.append(orderClause);
            } else {
                selectJpql.append(" order by tp.seqno asc");
            }
        } else {
            selectJpql.append(" order by tp.seqno asc");
        }

        var query = entityManager.createQuery(selectJpql.toString(), JmpTripPlanEntity.class);
        parameters.forEach(query::setParameter);

        int start = (int) pageable.getOffset();
        int length = pageable.getPageSize();
        
        query.setFirstResult(start);
        query.setMaxResults(length);

        List<JmpTripPlanEntity> list = query.getResultList();

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
