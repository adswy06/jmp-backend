package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.feature.tripplan.dto.request.RoadHazardCreateDto;
import com.pancaran.master.feature.tripplan.dto.request.RoadHazardUpdateDto;
import com.pancaran.master.feature.tripplan.entity.master.RoadHazardEntity;
import com.pancaran.master.feature.tripplan.service.RoadHazardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/road-hazards")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Road Hazards Management", description = "Endpoints for managing and querying road hazards (potholes, crime, floods, etc.)")
public class RoadHazardController {

    private final RoadHazardService service;

    @PostMapping
    @Operation(summary = "Create Road Hazard", description = "Creates a new road hazard with latitude and longitude. The database trigger automatically sets up the spatial PostGIS GIST geometry.")
    public ResponseEntity<APIResponse<RoadHazardEntity>> create(
            @RequestBody @Valid RoadHazardCreateDto request) {
        return ResponseEntity.ok(APIResponse.success(service.createHazard(request)));
    }

    @GetMapping
    @Operation(summary = "Get Road Hazards List", description = "Retrieves a list of all road hazards with optional filters for name, type, and active status.")
    public ResponseEntity<APIResponse<List<RoadHazardEntity>>> getAll(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "hazardType", required = false) String hazardType,
            @RequestParam(value = "isActive", required = false) Boolean isActive) {
        return ResponseEntity.ok(APIResponse.success(service.getAllHazards(name, hazardType, isActive)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Road Hazard by ID", description = "Retrieves details of a single road hazard.")
    public ResponseEntity<APIResponse<RoadHazardEntity>> getById(
            @PathVariable("id") String id) {
        return ResponseEntity.ok(APIResponse.success(service.getHazardById(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Road Hazard", description = "Updates attributes of a road hazard.")
    public ResponseEntity<APIResponse<RoadHazardEntity>> update(
            @PathVariable("id") String id,
            @RequestBody @Valid RoadHazardUpdateDto request) {
        return ResponseEntity.ok(APIResponse.success(service.updateHazard(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Road Hazard", description = "Deletes a road hazard from master data.")
    public ResponseEntity<APIResponse<String>> delete(
            @PathVariable("id") String id) {
        service.deleteHazard(id);
        return ResponseEntity.ok(APIResponse.success("Road hazard deleted successfully"));
    }
}
