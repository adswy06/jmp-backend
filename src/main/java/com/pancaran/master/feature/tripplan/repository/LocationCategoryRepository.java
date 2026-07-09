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

    public List<LocationCategoryEntity> findAllCategoriesWithActivities() {
        return entityManager.createQuery(
                "select distinct lc from LocationCategoryEntity lc left join fetch lc.activities order by lc.categoryName",
                LocationCategoryEntity.class)
                .getResultList();
    }

    public Optional<LocationCategoryEntity> findCategoryById(String id) {
        LocationCategoryEntity entity = entityManager.find(LocationCategoryEntity.class, id);
        return Optional.ofNullable(entity);
    }

    public Optional<LocationCategoryEntity> findCategoryByIdWithActivities(String id) {
        List<LocationCategoryEntity> list = entityManager.createQuery(
                "select distinct lc from LocationCategoryEntity lc left join fetch lc.activities a where lc.id = :id",
                LocationCategoryEntity.class)
                .setParameter("id", id)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<ActivityEntity> findActivitiesByCategoryId(String categoryId) {
        return entityManager.createQuery(
                "select a from LocationCategoryEntity lc join lc.activities a where lc.id = :categoryId order by a.name",
                ActivityEntity.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public List<ActivityEntity> findActivities(String categoryId, String name) {
        StringBuilder jpql = new StringBuilder("select distinct a from ActivityEntity a");
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            jpql.append(" join a.locationCategories lc where lc.id = :categoryId");
        } else {
            jpql.append(" where 1=1");
        }
        if (name != null && !name.trim().isEmpty()) {
            jpql.append(" and upper(a.name) like :name");
        }
        jpql.append(" order by a.name");

        var query = entityManager.createQuery(jpql.toString(), ActivityEntity.class);
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            query.setParameter("categoryId", categoryId);
        }
        if (name != null && !name.trim().isEmpty()) {
            query.setParameter("name", "%" + name.trim().toUpperCase() + "%");
        }
        return query.getResultList();
    }
}
