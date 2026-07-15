package com.pancaran.master.feature.jmp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.apik.core.data.dto.SearchInput;
import com.pancaran.master.feature.jmp.entity.*;
import com.pancaran.master.feature.tripplan.entity.master.CustomerView;
import com.pancaran.master.feature.tripplan.entity.master.ConsigneeView;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityCostEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.ActivityLeadTimeEntity;
import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import com.pancaran.master.feature.tripplan.entity.transaction.RoutePointEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JmpRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    @Autowired
    @Qualifier("jmp-dbJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

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

    public RoutePointEntity findRoutePointByRouteIdAndPoiIdAndSeqno(String routeId, String poiId, Integer seqno) {
        List<RoutePointEntity> list = entityManager.createQuery(
                "select rp from RoutePointEntity rp where rp.routeId = :routeId and rp.poiId = :poiId and rp.seqno = :seqno",
                RoutePointEntity.class)
                .setParameter("routeId", routeId)
                .setParameter("poiId", poiId)
                .setParameter("seqno", seqno)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
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

    private String getPlaceholders(List<String> ids) {
        return ids.stream().map(id -> "?").collect(Collectors.joining(","));
    }

    public JmpEntity findJmpById(String id) {
        String sql = "select j.id, j.customer_id, j.consignee_id, j.commercial_route, j.reference_no, j.title, j.description, j.status, j.is_notification_global, j.createdby, j.createdat, j.updatedby, j.updatedat, " +
                "c.name as cust_name, c.alias as cust_alias, c.isactive as cust_isactive, c.alt_name as cust_alt_name, c.prefix as cust_prefix, c.suffix as cust_suffix, c.description as cust_desc, c.updated as cust_updated, c.updatedby as cust_updatedby, c.isdeleted as cust_isdeleted, c.deleted as cust_deleted, c.lastsync as cust_lastsync, " +
                "cg.name as cg_name, cg.alias as cg_alias, cg.isactive as cg_isactive, cg.alt_name as cg_alt_name, cg.prefix as cg_prefix, cg.suffix as cg_suffix, cg.description as cg_desc, cg.updated as cg_updated, cg.updatedby as cg_updatedby, cg.isdeleted as cg_isdeleted, cg.deleted as cg_deleted, cg.lastsync as cg_lastsync " +
                "from t_jmp j " +
                "left join em_customer c on c.id = j.customer_id " +
                "left join em_consignee cg on cg.id = j.consignee_id " +
                "where j.id = ?";
        List<JmpEntity> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpEntity j = new JmpEntity();
            j.setId(rs.getString("id"));
            j.setCustomerId(rs.getString("customer_id"));
            j.setConsigneeId(rs.getString("consignee_id"));
            j.setCommercialRoute(rs.getString("commercial_route"));
            j.setReferenceNo(rs.getString("reference_no"));
            j.setTitle(rs.getString("title"));
            j.setDescription(rs.getString("description"));
            j.setStatus(rs.getString("status"));
            j.setIsNotificationGlobal(rs.getObject("is_notification_global", Boolean.class));
            j.setCreatedBy(rs.getString("createdby"));
            j.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null);
            j.setUpdatedBy(rs.getString("updatedby"));
            j.setUpdatedAt(rs.getTimestamp("updatedat") != null ? rs.getTimestamp("updatedat").toLocalDateTime() : null);

            if (j.getCustomerId() != null) {
                CustomerView cust = new CustomerView();
                cust.setId(j.getCustomerId());
                cust.setName(rs.getString("cust_name"));
                cust.setAlias(rs.getString("cust_alias"));
                cust.setIsactive(rs.getObject("cust_isactive", Boolean.class));
                cust.setAltName(rs.getString("cust_alt_name"));
                cust.setPrefix(rs.getString("cust_prefix"));
                cust.setSuffix(rs.getString("cust_suffix"));
                cust.setDescription(rs.getString("cust_desc"));
                cust.setUpdated(rs.getTimestamp("cust_updated") != null ? rs.getTimestamp("cust_updated").toLocalDateTime() : null);
                cust.setUpdatedBy(rs.getString("cust_updatedby"));
                cust.setIsdeleted(rs.getObject("cust_isdeleted", Boolean.class));
                cust.setDeleted(rs.getTimestamp("cust_deleted") != null ? rs.getTimestamp("cust_deleted").toLocalDateTime() : null);
                cust.setLastsync(rs.getTimestamp("cust_lastsync") != null ? rs.getTimestamp("cust_lastsync").toLocalDateTime() : null);
                j.setCustomer(cust);
            }

            if (j.getConsigneeId() != null) {
                ConsigneeView cg = new ConsigneeView();
                cg.setId(j.getConsigneeId());
                cg.setName(rs.getString("cg_name"));
                cg.setAlias(rs.getString("cg_alias"));
                cg.setIsactive(rs.getObject("cg_isactive", Boolean.class));
                cg.setAltName(rs.getString("cg_alt_name"));
                cg.setPrefix(rs.getString("cg_prefix"));
                cg.setSuffix(rs.getString("cg_suffix"));
                cg.setDescription(rs.getString("cg_desc"));
                cg.setUpdated(rs.getTimestamp("cg_updated") != null ? rs.getTimestamp("cg_updated").toLocalDateTime() : null);
                cg.setUpdatedBy(rs.getString("cg_updatedby"));
                cg.setIsdeleted(rs.getObject("cg_isdeleted", Boolean.class));
                cg.setDeleted(rs.getTimestamp("cg_deleted") != null ? rs.getTimestamp("cg_deleted").toLocalDateTime() : null);
                cg.setLastsync(rs.getTimestamp("cg_lastsync") != null ? rs.getTimestamp("cg_lastsync").toLocalDateTime() : null);
                j.setConsignee(cg);
            }
            return j;
        }, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<JmpUnitEntity> findUnitsByJmpId(String jmpId) {
        String sql = "select id, jmp_id, unit_type_id from t_jmp_unit where jmp_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpUnitEntity u = new JmpUnitEntity();
            u.setId(rs.getString("id"));
            u.setJmpId(rs.getString("jmp_id"));
            u.setUnitTypeId(rs.getString("unit_type_id"));
            return u;
        }, jmpId);
    }

    public List<JmpTripPlanEntity> findTripPlansByJmpId(String jmpId) {
        String sql = "select id, jmp_id, route_id, seqno, transport_mode, remarks, createdat from t_jmp_trip_plan where jmp_id = ? order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpTripPlanEntity tp = new JmpTripPlanEntity();
            tp.setId(rs.getString("id"));
            tp.setJmpId(rs.getString("jmp_id"));
            tp.setRouteId(rs.getString("route_id"));
            tp.setSeqno(rs.getObject("seqno", Integer.class));
            tp.setTransportMode(rs.getString("transport_mode"));
            tp.setRemarks(rs.getString("remarks"));
            tp.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null);
            return tp;
        }, jmpId);
    }

    public List<JmpSeaEntity> findSeaByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, origin_port_id, destination_port_id, vessel_id, etd, eta from t_jmp_sea where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpSeaEntity s = new JmpSeaEntity();
            s.setId(rs.getString("id"));
            s.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            s.setOriginPortId(rs.getString("origin_port_id"));
            s.setDestinationPortId(rs.getString("destination_port_id"));
            s.setVesselId(rs.getString("vessel_id"));
            s.setEtd(rs.getTimestamp("etd") != null ? rs.getTimestamp("etd").toLocalDateTime() : null);
            s.setEta(rs.getTimestamp("eta") != null ? rs.getTimestamp("eta").toLocalDateTime() : null);
            return s;
        }, tripPlanIds.toArray());
    }

    public List<JmpAirEntity> findAirByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, origin_airport_id, destination_airport_id, airline, flight_no, etd, eta from t_jmp_air where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpAirEntity a = new JmpAirEntity();
            a.setId(rs.getString("id"));
            a.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            a.setOriginAirportId(rs.getString("origin_airport_id"));
            a.setDestinationAirportId(rs.getString("destination_airport_id"));
            a.setAirline(rs.getString("airline"));
            a.setFlightNo(rs.getString("flight_no"));
            a.setEtd(rs.getTimestamp("etd") != null ? rs.getTimestamp("etd").toLocalDateTime() : null);
            a.setEta(rs.getTimestamp("eta") != null ? rs.getTimestamp("eta").toLocalDateTime() : null);
            return a;
        }, tripPlanIds.toArray());
    }

    public List<JmpDriverEntity> findDriversByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, driver_id, seqno from t_jmp_driver where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ") order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpDriverEntity d = new JmpDriverEntity();
            d.setId(rs.getString("id"));
            d.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            d.setDriverId(rs.getString("driver_id"));
            d.setSeqno(rs.getObject("seqno", Integer.class));
            return d;
        }, tripPlanIds.toArray());
    }

    public List<JmpExtraCostEntity> findExtraCostsByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, name, amount, remarks from t_jmp_extra_cost where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpExtraCostEntity ec = new JmpExtraCostEntity();
            ec.setId(rs.getString("id"));
            ec.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            ec.setName(rs.getString("name"));
            ec.setAmount(rs.getBigDecimal("amount"));
            ec.setRemarks(rs.getString("remarks"));
            return ec;
        }, tripPlanIds.toArray());
    }

    public List<JmpRoutePointEntity> findRoutePointsByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, route_point_id, poi_id, seqno, alias, address, iscustom, paths from t_jmp_route_point where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ") order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpRoutePointEntity rp = new JmpRoutePointEntity();
            rp.setId(rs.getString("id"));
            rp.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            rp.setRoutePointId(rs.getString("route_point_id"));
            rp.setPoiId(rs.getString("poi_id"));
            rp.setSeqno(rs.getObject("seqno", Integer.class));
            rp.setAlias(rs.getString("alias"));
            rp.setAddress(rs.getString("address"));
            rp.setIsCustom(rs.getObject("iscustom", Boolean.class));
            rp.setPaths(rs.getString("paths"));
            return rp;
        }, tripPlanIds.toArray());
    }

    public List<JmpRouteSegmentEntity> findRouteSegmentsByTripPlanIds(List<String> tripPlanIds) {
        if (tripPlanIds == null || tripPlanIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_trip_plan_id, start_route_point_id, end_route_point_id, seqno, remarks from t_jmp_route_segment where jmp_trip_plan_id in (" + getPlaceholders(tripPlanIds) + ") order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpRouteSegmentEntity seg = new JmpRouteSegmentEntity();
            seg.setId(rs.getString("id"));
            seg.setJmpTripPlanId(rs.getString("jmp_trip_plan_id"));
            seg.setStartRoutePointId(rs.getString("start_route_point_id"));
            seg.setEndRoutePointId(rs.getString("end_route_point_id"));
            seg.setSeqno(rs.getObject("seqno", Integer.class));
            seg.setRemarks(rs.getString("remarks"));
            return seg;
        }, tripPlanIds.toArray());
    }

    public List<JmpActivityEntity> findActivitiesByRoutePointIds(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_route_point_id, activity_id, activity_name, leadtime, cost, seqno, remarks, is_notification, notes from t_jmp_activity where jmp_route_point_id in (" + getPlaceholders(routePointIds) + ") order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpActivityEntity act = new JmpActivityEntity();
            act.setId(rs.getString("id"));
            act.setJmpRoutePointId(rs.getString("jmp_route_point_id"));
            act.setActivityId(rs.getString("activity_id"));
            act.setActivityName(rs.getString("activity_name"));
            act.setLeadtime(rs.getObject("leadtime", Integer.class));
            act.setCost(rs.getBigDecimal("cost"));
            act.setSeqno(rs.getObject("seqno", Integer.class));
            act.setRemarks(rs.getString("remarks"));
            act.setIsNotification(rs.getObject("is_notification", Boolean.class));
            act.setNotes(rs.getString("notes"));
            return act;
        }, routePointIds.toArray());
    }

    public List<JmpRouteSegmentUnitEntity> findSegmentUnitsBySegmentIds(List<String> routeSegmentIds) {
        if (routeSegmentIds == null || routeSegmentIds.isEmpty()) return Collections.emptyList();
        String sql = "select id, jmp_route_segment_id, unit_type_id from t_jmp_route_segment_unit where jmp_route_segment_id in (" + getPlaceholders(routeSegmentIds) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            JmpRouteSegmentUnitEntity su = new JmpRouteSegmentUnitEntity();
            su.setId(rs.getString("id"));
            su.setJmpRouteSegmentId(rs.getString("jmp_route_segment_id"));
            su.setUnitTypeId(rs.getString("unit_type_id"));
            return su;
        }, routeSegmentIds.toArray());
    }
}
