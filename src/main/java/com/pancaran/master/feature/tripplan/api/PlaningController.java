package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.tripplan.entity.transaction.RouteEntity;
import com.pancaran.master.feature.jmp.service.JmpService;
import com.pancaran.master.feature.tripplan.dto.request.RouteCreateRequestDto;
import com.pancaran.master.feature.tripplan.dto.request.PathCoordinatesDto;
import com.pancaran.master.feature.tripplan.service.PlaningService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.apik.core.common.FeatureOperation;
import com.apik.core.data.dto.impl.SearchInputImpl;
import org.springdoc.core.annotations.ParameterObject;
import com.pancaran.master.feature.tripplan.mapper.RouteAggregate;
import com.pancaran.master.feature.tripplan.dto.response.RouteResponseDto;

@RestController
@RequestMapping("/api/planing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Route Planning", description = "Endpoints for creating and enriching route plans")
public class PlaningController {

    private final PlaningService planingService;
    private final JmpService jmpService;

    @PostMapping
    @Operation(summary = "Create and Enrich Route Plan", description = "Processes, enriches, and stores a new trip planning route. Automatically sets distances, basic costs, UUIDs for region POIs, and PostGIS geofence boundaries.")
    public ResponseEntity<APIResponse<RouteResponseDto>> planning(@RequestBody @Valid RouteCreateRequestDto request) {
        RouteResponseDto saved = planingService.planRoute(request);
        return ResponseEntity.ok(APIResponse.success(saved));
    }

    @Operation(summary = "Listing, Searching & Filtering Planned Routes Data")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RouteEntity>> getRoutePage(
            @ParameterObject @PageableDefault(page = 0, size = 20) Pageable pageable,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "filter", required = false) String[] filter) throws Exception {
        SearchInputImpl input = new SearchInputImpl(null, FeatureOperation.SEARCH);
        input.pageable(pageable, search, filter);
        return ResponseEntity.ok(planingService.getRoutePage(input));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Route Plan by ID", description = "Retrieves the complete detail of a planned route by its ID, including points, geofences, activities, segments, and units in an optimized fetch.")
    public ResponseEntity<APIResponse<RouteResponseDto>> getRouteById(@PathVariable("id") String id) {
        return ResponseEntity.ok(APIResponse.success(planingService.getRouteById(id)));
    }

    @PostMapping("/detect-hazards")
    @Operation(summary = "Detect hazards along coordinates path", description = "Lightweight spatial PostGIS query that returns a list of hazards along the given coordinate path (useful for real-time drag and drop).")
    public ResponseEntity<APIResponse<List<RouteResponseDto.RoadHazardResponseDto>>> detectHazards(@RequestBody PathCoordinatesDto request) {
        return ResponseEntity.ok(APIResponse.success(planingService.detectHazards(request)));
    }

    /*
    @GetMapping("/test-query")
    @Operation(summary = "Get internal spatial query test", description = "Returns total hazards and distance calculations for the test path.")
    public ResponseEntity<APIResponse<Object>> testQuery() {
        return ResponseEntity.ok(APIResponse.success(planingService.testDatabaseQuery()));
    }
    */
}
