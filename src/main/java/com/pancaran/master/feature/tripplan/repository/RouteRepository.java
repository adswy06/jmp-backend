package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.*;
import com.pancaran.master.feature.tripplan.entity.transaction.*;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.dto.response.RouteResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.apik.core.data.dto.SearchInput;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RouteRepository {
    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    @Autowired
    @Qualifier("jmp-dbJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

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

    public RouteEntity findRouteById(String id) {
        String sql = "select id, alias, name, distance_km, journey_leadtime, basic_cost, status, isactive, isdeleted, deletedat, createdby, createdat, updatedby, updatedat from m_route where id = ? and isdeleted = false";
        List<RouteEntity> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteEntity r = new RouteEntity();
            r.setId(rs.getString("id"));
            r.setAlias(rs.getString("alias"));
            r.setName(rs.getString("name"));
            r.setDistanceKm(rs.getObject("distance_km") != null ? rs.getDouble("distance_km") : null);
            r.setJourneyLeadTime(rs.getObject("journey_leadtime") != null ? rs.getInt("journey_leadtime") : null);
            r.setBasicCost(rs.getObject("basic_cost") != null ? rs.getDouble("basic_cost") : null);
            r.setStatus(rs.getString("status"));
            r.setActive(rs.getObject("isactive", Boolean.class));
            r.setDeleted(rs.getObject("isdeleted", Boolean.class));
            r.setDeletedAt(rs.getTimestamp("deletedat") != null ? rs.getTimestamp("deletedat").toLocalDateTime() : null);
            r.setCreatedBy(rs.getString("createdby"));
            r.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null);
            r.setUpdatedBy(rs.getString("updatedby"));
            r.setUpdatedAt(rs.getTimestamp("updatedat") != null ? rs.getTimestamp("updatedat").toLocalDateTime() : null);
            return r;
        }, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<RouteEntity> findRoutesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, alias, name, distance_km, journey_leadtime, basic_cost, status, isactive, isdeleted, deletedat, createdby, createdat, updatedby, updatedat from m_route where id in (" + placeholders + ") and isdeleted = false";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteEntity r = new RouteEntity();
            r.setId(rs.getString("id"));
            r.setAlias(rs.getString("alias"));
            r.setName(rs.getString("name"));
            r.setDistanceKm(rs.getObject("distance_km") != null ? rs.getDouble("distance_km") : null);
            r.setJourneyLeadTime(rs.getObject("journey_leadtime") != null ? rs.getInt("journey_leadtime") : null);
            r.setBasicCost(rs.getObject("basic_cost") != null ? rs.getDouble("basic_cost") : null);
            r.setStatus(rs.getString("status"));
            r.setActive(rs.getObject("isactive", Boolean.class));
            r.setDeleted(rs.getObject("isdeleted", Boolean.class));
            r.setDeletedAt(rs.getTimestamp("deletedat") != null ? rs.getTimestamp("deletedat").toLocalDateTime() : null);
            r.setCreatedBy(rs.getString("createdby"));
            r.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null);
            r.setUpdatedBy(rs.getString("updatedby"));
            r.setUpdatedAt(rs.getTimestamp("updatedat") != null ? rs.getTimestamp("updatedat").toLocalDateTime() : null);
            return r;
        }, ids.toArray());
    }

    public List<RoutePointEntity> findRoutePointsByRouteId(String routeId) {
        String sql = "select id, ref_id_route, checksum, poi_id, seqno, alias, address, paths, iszone from m_route_point where ref_id_route = ? order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RoutePointEntity rp = new RoutePointEntity();
            rp.setId(rs.getString("id"));
            rp.setRouteId(rs.getString("ref_id_route"));
            rp.setChecksum(rs.getString("checksum"));
            rp.setPoiId(rs.getString("poi_id"));
            rp.setSeqno(rs.getObject("seqno", Integer.class));
            rp.setAlias(rs.getString("alias"));
            rp.setAddress(rs.getString("address"));
            rp.setPaths(rs.getString("paths"));
            rp.setIszone(rs.getObject("iszone", Boolean.class));
            return rp;
        }, routeId);
    }

    public List<RoutePointEntity> findRoutePointsByRouteIds(List<String> routeIds) {
        if (routeIds == null || routeIds.isEmpty()) return Collections.emptyList();
        String placeholders = routeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, ref_id_route, checksum, poi_id, seqno, alias, address, paths, iszone from m_route_point where ref_id_route in (" + placeholders + ") order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RoutePointEntity rp = new RoutePointEntity();
            rp.setId(rs.getString("id"));
            rp.setRouteId(rs.getString("ref_id_route"));
            rp.setChecksum(rs.getString("checksum"));
            rp.setPoiId(rs.getString("poi_id"));
            rp.setSeqno(rs.getObject("seqno", Integer.class));
            rp.setAlias(rs.getString("alias"));
            rp.setAddress(rs.getString("address"));
            rp.setPaths(rs.getString("paths"));
            rp.setIszone(rs.getObject("iszone", Boolean.class));
            return rp;
        }, routeIds.toArray());
    }

    public List<GeofenceEntity> findGeofencesByRouteId(String routeId) {
        String sql = "select id, poi_id, route_id, shapetype, radius, paths from geofence where route_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            GeofenceEntity g = new GeofenceEntity();
            g.setId(rs.getString("id"));
            g.setPoiId(rs.getString("poi_id"));
            g.setRouteId(rs.getString("route_id"));
            g.setShapeType(rs.getString("shapetype"));
            g.setRadius(rs.getObject("radius", Integer.class));
            g.setPaths(rs.getString("paths"));
            return g;
        }, routeId);
    }

    public List<ActivityLeadTimeEntity> findLeadTimesByRoutePointIds(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        String placeholders = routePointIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, route_point_id, activity_id, leadtime from m_activity_lead_time where route_point_id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ActivityLeadTimeEntity alt = new ActivityLeadTimeEntity();
            alt.setId(rs.getString("id"));
            alt.setRoutePointId(rs.getString("route_point_id"));
            alt.setActivityId(rs.getString("activity_id"));
            alt.setLeadtime(rs.getObject("leadtime", Integer.class));
            return alt;
        }, routePointIds.toArray());
    }

    public List<ActivityCostEntity> findActivityCostsByRoutePointIds(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        String placeholders = routePointIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, route_point_id, activity_id, amount from m_activity_cost where route_point_id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ActivityCostEntity ac = new ActivityCostEntity();
            ac.setId(rs.getString("id"));
            ac.setRoutePointId(rs.getString("route_point_id"));
            ac.setActivityId(rs.getString("activity_id"));
            ac.setAmount(rs.getObject("amount") != null ? rs.getDouble("amount") : null);
            return ac;
        }, routePointIds.toArray());
    }

    public List<ActivityExtraCostEntity> findExtraCostsByRoutePointIds(List<String> routePointIds) {
        if (routePointIds == null || routePointIds.isEmpty()) return Collections.emptyList();
        String placeholders = routePointIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, route_point_id, activity_id, name, amount from m_activity_extra_cost where route_point_id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ActivityExtraCostEntity aec = new ActivityExtraCostEntity();
            aec.setId(rs.getString("id"));
            aec.setRoutePointId(rs.getString("route_point_id"));
            aec.setActivityId(rs.getString("activity_id"));
            aec.setName(rs.getString("name"));
            aec.setAmount(rs.getObject("amount") != null ? rs.getDouble("amount") : null);
            return aec;
        }, routePointIds.toArray());
    }

    public List<RouteSegmentEntity> findRouteSegmentsByRouteId(String routeId) {
        String sql = "select id, route_id, start_route_point_id, end_route_point_id, seqno, remarks from m_route_segment where route_id = ? order by seqno asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteSegmentEntity seg = new RouteSegmentEntity();
            seg.setId(rs.getString("id"));
            seg.setRouteId(rs.getString("route_id"));
            seg.setStartRoutePointId(rs.getString("start_route_point_id"));
            seg.setEndRoutePointId(rs.getString("end_route_point_id"));
            seg.setSeqno(rs.getObject("seqno", Integer.class));
            seg.setRemarks(rs.getString("remarks"));
            return seg;
        }, routeId);
    }

    public List<RouteSegmentUnitEntity> findRouteSegmentUnitsBySegmentIds(List<String> segmentIds) {
        if (segmentIds == null || segmentIds.isEmpty()) return Collections.emptyList();
        String placeholders = segmentIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, route_segment_id, unit_type_id from m_route_segment_unit where route_segment_id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteSegmentUnitEntity rsu = new RouteSegmentUnitEntity();
            rsu.setId(rs.getString("id"));
            rsu.setRouteSegmentId(rs.getString("route_segment_id"));
            rsu.setUnitTypeId(rs.getString("unit_type_id"));
            return rsu;
        }, segmentIds.toArray());
    }

    public List<PoiEntity> findPoisByIdsNative(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, gpoi_id, region_code, zone_id, location_category, name, address, lng, lat, isactive, createdby, createdat, updatedby, updatedat from m_poi where id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PoiEntity poi = new PoiEntity();
            poi.setId(rs.getString("id"));
            poi.setGpoiId(rs.getString("gpoi_id"));
            poi.setRegionCode(rs.getString("region_code"));
            poi.setZone(rs.getString("zone_id"));
            poi.setName(rs.getString("name"));
            poi.setAddress(rs.getString("address"));
            poi.setLng(rs.getObject("lng") != null ? rs.getDouble("lng") : null);
            poi.setLat(rs.getObject("lat") != null ? rs.getDouble("lat") : null);
            poi.setIsactive(rs.getObject("isactive", Boolean.class));
            poi.setCreatedBy(rs.getString("createdby"));
            poi.setCreatedAt(rs.getTimestamp("createdat") != null ? rs.getTimestamp("createdat").toLocalDateTime() : null);
            poi.setUpdatedBy(rs.getString("updatedby"));
            poi.setUpdatedAt(rs.getTimestamp("updatedat") != null ? rs.getTimestamp("updatedat").toLocalDateTime() : null);

            String locCat = rs.getString("location_category");
            if (locCat != null) {
                LocationCategoryEntity lc = new LocationCategoryEntity();
                lc.setCategoryName(locCat);
                poi.setLocationCategory(lc);
            }
            return poi;
        }, ids.toArray());
    }

    public List<ZoneEntity> findZonesByIdsNative(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, name, address, paths, created_at, updated_at, created_by, updated_by from m_zone where id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ZoneEntity zone = new ZoneEntity();
            zone.setId(rs.getString("id"));
            zone.setName(rs.getString("name"));
            zone.setAddress(rs.getString("address"));
            zone.setPaths(rs.getString("paths"));
            zone.setCreatedBy(rs.getString("created_by"));
            zone.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            zone.setUpdatedBy(rs.getString("updated_by"));
            zone.setUpdatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return zone;
        }, ids.toArray());
    }

    public List<ActivityEntity> findActivitiesByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select id, category_name, name, cost, lead_time from m_activity where id in (" + placeholders + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ActivityEntity a = new ActivityEntity();
            a.setId(rs.getString("id"));
            a.setCategoryName(rs.getString("category_name"));
            a.setName(rs.getString("name"));
            a.setCost(rs.getBigDecimal("cost"));
            a.setLeadTime(rs.getObject("lead_time", Integer.class));
            return a;
        }, ids.toArray());
    }

    public List<RouteResponseDto.RoadHazardResponseDto> findHazardsByPoi(Double lat, Double lng, double proximityMeters) {
        if (lat == null || lng == null) return Collections.emptyList();
        String sql = "SELECT id, name, hazard_type, severity, radius, latitude, longitude, " +
                     "ST_Distance(ST_Transform(geom, 3857), ST_Transform(ST_SetSRID(ST_Point(?, ?), 4326), 3857)) as distance_meters " +
                     "FROM m_road_hazard " +
                     "WHERE is_active = true " +
                     "AND ST_DWithin(ST_Transform(geom, 3857), ST_Transform(ST_SetSRID(ST_Point(?, ?), 4326), 3857), radius + ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteResponseDto.RoadHazardResponseDto h = new RouteResponseDto.RoadHazardResponseDto();
            h.setId(rs.getString("id"));
            h.setName(rs.getString("name"));
            h.setHazardType(rs.getString("hazard_type"));
            h.setSeverity(rs.getString("severity"));
            h.setRadius(rs.getObject("radius", Integer.class));
            h.setLatitude(rs.getDouble("latitude"));
            h.setLongitude(rs.getDouble("longitude"));
            h.setDistanceFromRoute(rs.getDouble("distance_meters"));
            return h;
        }, lng, lat, lng, lat, proximityMeters);
    }

    public List<RouteResponseDto.RoadHazardResponseDto> findHazardsByLineString(String lineString) {
        if (lineString == null || lineString.trim().isEmpty()) return Collections.emptyList();
        String sql = "SELECT id, name, hazard_type, severity, radius, latitude, longitude, " +
                     "ST_Distance(ST_Transform(geom, 3857), ST_Transform(ST_GeomFromText(?, 4326), 3857)) as distance_meters " +
                     "FROM m_road_hazard " +
                     "WHERE is_active = true " +
                     "AND ST_DWithin(ST_Transform(geom, 3857), ST_Transform(ST_GeomFromText(?, 4326), 3857), radius)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            RouteResponseDto.RoadHazardResponseDto h = new RouteResponseDto.RoadHazardResponseDto();
            h.setId(rs.getString("id"));
            h.setName(rs.getString("name"));
            h.setHazardType(rs.getString("hazard_type"));
            h.setSeverity(rs.getString("severity"));
            h.setRadius(rs.getObject("radius", Integer.class));
            h.setLatitude(rs.getDouble("latitude"));
            h.setLongitude(rs.getDouble("longitude"));
            h.setDistanceFromRoute(rs.getDouble("distance_meters"));
            return h;
        }, lineString, lineString);
    }
}
