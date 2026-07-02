package com.pancaran.master.feature.tripplan.repository;

import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.master.LocationCategoryEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class LocationCategoryRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    public List<LocationCategoryEntity> findAllCategories() {
        return entityManager.createQuery(
                "select lc from LocationCategoryEntity lc order by lc.categoryName",
                LocationCategoryEntity.class)
                .getResultList();
    }

    public Optional<LocationCategoryEntity> findCategoryById(String id) {
        LocationCategoryEntity entity = entityManager.find(LocationCategoryEntity.class, id);
        return Optional.ofNullable(entity);
    }

    public List<ActivityEntity> findActivitiesByCategoryId(String categoryId) {
        return entityManager.createQuery(
                "select a from LocationCategoryEntity lc join lc.activities a where lc.id = :categoryId order by a.name",
                ActivityEntity.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }
}
