package com.pancaran.master.feature.tripplan.service.persister;

import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PlaningPersister {

    private final RouteRepository routeRepository;

    @Transactional(value = "jmp-dbTransactionManager")
    public void persist(RouteAggregate aggregate) {
        routeRepository.save(aggregate);
    }
}
