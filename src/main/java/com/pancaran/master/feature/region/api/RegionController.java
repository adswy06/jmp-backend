package com.pancaran.master.feature.region.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.common.ApiException;
import com.pancaran.master.feature.region.dto.*;
import com.pancaran.master.feature.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Regions (Wilayah Indonesia)", description = "Endpoints for querying Indonesian administrative divisions and PostGIS geometries")
public class RegionController {

    private final RegionService service;

    @GetMapping
    @Operation(summary = "Get Region Tree", description = "Retrieves the hierarchical tree of administrative regions without geometries. Can be filtered by code, search query, and supports paginated scroll.")
    public ResponseEntity<APIResponse<List<RegionTreeDto>>> getRegionTree(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        return ResponseEntity.ok(APIResponse.success(service.getRegionTree(code, search, page, size)));
    }

    @GetMapping("/geom")
    @Operation(summary = "Get Region Geometry by Code", description = "Retrieves only the PostGIS GeoJSON geometry string for any administrative region code (2-digit Province, 4-digit Regency, 6-digit District, 10-digit Village).")
    public ResponseEntity<String> getRegionGeom(
            @RequestParam("code") String code) {
        String geom = service.getRegionGeom(code);
        if (geom == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(geom);
    }
}
