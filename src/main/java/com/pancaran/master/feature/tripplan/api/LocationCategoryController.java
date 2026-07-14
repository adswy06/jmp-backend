package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.tripplan.dto.response.ActivityResponseDto;
import com.pancaran.master.feature.tripplan.dto.response.LocationCategoryWithActivitiesDto;
import com.pancaran.master.feature.tripplan.service.LocationCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/location-categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Location Categories & Activities", description = "Endpoints for retrieving location categories and their activities")
public class LocationCategoryController {

    private final LocationCategoryService service;

    // Endpoint untuk mendapatkan daftar kategori lokasi beserta aktivitasnya (header-detail)
    @GetMapping
    @Operation(summary = "Get Hierarchical Location Categories", description = "Retrieves a hierarchical list of location categories, each with its nested list of activities (header-detail). Supports optional filtering by category ID.")
    public ResponseEntity<APIResponse<List<LocationCategoryWithActivitiesDto>>> getLocationCategories(
            @RequestParam(value = "categoryId", required = false) String categoryId) {
        return ResponseEntity.ok(APIResponse.success(service.getLocationCategoriesWithActivities(categoryId)));
    }

    // Endpoint untuk mendapatkan daftar detail aktivitas saja secara flat
    @GetMapping("/activities")
    @Operation(summary = "Get Flat Activities List", description = "Retrieves a flat list of activities, including their default cost and leadTime configurations. Supports filtering by category ID and/or activity name.")
    public ResponseEntity<APIResponse<List<ActivityResponseDto>>> getActivities(
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "name", required = false) String name) {
        return ResponseEntity.ok(APIResponse.success(service.getActivities(categoryId, name)));
    }
}
