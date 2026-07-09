package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.PoiEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PoiRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    public List<PoiEntity> findPois(String name, String category, Boolean isactive) {
        StringBuilder jpql = new StringBuilder("select p from PoiEntity p left join fetch p.locationCategory lc where 1=1");
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(p.name) like :name");
        }
        if (category != null && !category.trim().isEmpty()) {
            jpql.append(" and (upper(p.locationCategory.categoryName) = :category or p.locationCategory.id = :category)");
        }
        if (isactive != null) {
            jpql.append(" and p.isactive = :isactive");
        }
        jpql.append(" order by p.name");

        var query = entityManager.createQuery(jpql.toString(), PoiEntity.class);
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            query.setParameter("category", category.trim().toUpperCase());
        }
        if (isactive != null) {
            query.setParameter("isactive", isactive);
        }
        return query.getResultList();
    }

    public Optional<PoiEntity> findPoiById(String id) {
        List<PoiEntity> list = entityManager.createQuery(
                "select p from PoiEntity p left join fetch p.locationCategory where p.id = :id", PoiEntity.class)
                .setParameter("id", id)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<com.pancaran.master.feature.tripplan.entity.transaction.GeofenceEntity> findGeofencesByPoiIds(List<String> poiIds) {
        if (poiIds == null || poiIds.isEmpty()) return java.util.Collections.emptyList();
        return entityManager.createQuery(
                "select g from GeofenceEntity g where g.poiId in :poiIds",
                com.pancaran.master.feature.tripplan.entity.transaction.GeofenceEntity.class)
                .setParameter("poiIds", poiIds)
                .getResultList();
    }
}
