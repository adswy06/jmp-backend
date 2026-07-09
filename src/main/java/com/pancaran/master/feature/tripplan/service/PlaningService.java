package com.pancaran.master.feature.tripplan.service;

import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregateMapper;
import com.pancaran.master.feature.tripplan.service.enricher.PlaningEnricher;
import com.pancaran.master.feature.tripplan.service.persister.PlaningPersister;
import com.pancaran.master.feature.tripplan.service.processor.PlaningProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import com.apik.core.data.dto.SearchInput;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteEntity;
import com.pancaran.master.feature.tripplan.repository.RouteRepository;

@Service
@RequiredArgsConstructor
public class PlaningService {

    private final RouteAggregateMapper mapper;
    private final PlaningEnricher enricher;
    private final PlaningProcessor processor;
    private final PlaningPersister persister;
    private final RouteRepository routeRepository;

    public RouteAggregate planRoute(RouteCreateRequestDto request) {
        RouteAggregate aggregate = mapper.toAggregate(request);

        enricher.enrich(aggregate);

        processor.process(aggregate);

        persister.persist(aggregate);

        return aggregate;
    }

    public Page<RouteEntity> getRoutePage(SearchInput input) {
        return routeRepository.findRoutePage(input);
    }
}
