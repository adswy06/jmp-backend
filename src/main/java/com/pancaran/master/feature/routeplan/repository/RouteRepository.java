package com.pancaran.master.feature.routeplan.repository;

import com.pancaran.master.feature.routeplan.entity.master.MstRouteEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RouteRepository {

    @PersistenceContext(unitName = "jmp-dbEntityManagerFactory")
    private EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

    public RouteRepository(@Qualifier("jmp-dbJdbcTemplate") JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    // -- JPA
    public MstRouteEntity save(MstRouteEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();

        return entity;
    }

    public Optional<MstRouteEntity> findById(String id) {
        return Optional.ofNullable(
                entityManager.find(MstRouteEntity.class, id)
        );
    }
}
