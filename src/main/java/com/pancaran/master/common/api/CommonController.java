package com.pancaran.master.common.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.common.dto.DropdownResponseDto;
import com.pancaran.master.feature.region.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Common Endpoints", description = "Endpoints for shared/common dropdown options")
public class CommonController {

    private final RegionService regionService;

    @GetMapping("/regions")
    @Operation(summary = "Get Regions Dropdown", description = "Retrieves a flat list of administrative divisions mapped as ID and Value supporting infinite scroll.")
    public ResponseEntity<APIResponse<List<DropdownResponseDto<String>>>> getRegionsDropdown(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ResponseEntity.ok(APIResponse.success(regionService.getRegionDropdownCommon(search, page, size)));
    }
}
