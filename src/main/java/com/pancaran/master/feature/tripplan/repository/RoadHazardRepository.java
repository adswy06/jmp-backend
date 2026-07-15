package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.RoadHazardEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RoadHazardRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    public RoadHazardEntity save(RoadHazardEntity entity, boolean isNew) {
        if (isNew) {
            entityManager.persist(entity);
            return entity;
        } else {
            return entityManager.merge(entity);
        }
    }

    public Optional<RoadHazardEntity> findById(String id) {
        RoadHazardEntity entity = entityManager.find(RoadHazardEntity.class, id);
        return Optional.ofNullable(entity);
    }

    public List<RoadHazardEntity> findAll(String name, String hazardType, Boolean isActive) {
        StringBuilder jpql = new StringBuilder("select rh from RoadHazardEntity rh where 1=1");
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(rh.name) like :name");
        }
        if (hazardType != null && !hazardType.trim().isEmpty()) {
            jpql.append(" and upper(rh.hazardType) = :hazardType");
        }
        if (isActive != null) {
            jpql.append(" and rh.active = :isActive");
        }
        jpql.append(" order by rh.name");

        var query = entityManager.createQuery(jpql.toString(), RoadHazardEntity.class);
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        if (hazardType != null && !hazardType.trim().isEmpty()) {
            query.setParameter("hazardType", hazardType.trim().toUpperCase());
        }
        if (isActive != null) {
            query.setParameter("isActive", isActive);
        }
        return query.getResultList();
    }

    public void delete(RoadHazardEntity entity) {
        entityManager.remove(entity);
    }

    public void flush() {
        entityManager.flush();
    }
}
