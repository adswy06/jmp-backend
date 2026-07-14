package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.tripplan.dto.response.PoiResponseDto;
import com.pancaran.master.feature.tripplan.service.PoiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pois")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Points of Interest (POI)", description = "Endpoints for managing and querying POIs")
public class PoiController {

    private final PoiService service;

    @GetMapping
    @Operation(summary = "Get POIs List", description = "Retrieves a list of Points of Interest (POIs) with optional filters for name, category, and active status. Eagerly loads category details in a single query.")
    public ResponseEntity<APIResponse<List<PoiResponseDto>>> getPois(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "isactive", required = false) Boolean isactive) {
        return ResponseEntity.ok(APIResponse.success(service.getPois(name, category, isactive)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get POI by ID", description = "Retrieves the details of a single POI by its ID. Includes the custom or administrative PostGIS fallback geofences.")
    public ResponseEntity<APIResponse<PoiResponseDto>> getPoiById(@PathVariable("id") String id) {
        return ResponseEntity.ok(APIResponse.success(service.getPoiById(id)));
    }
}
