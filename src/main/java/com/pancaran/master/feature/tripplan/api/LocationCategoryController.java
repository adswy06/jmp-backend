package com.pancaran.master.feature.tripplan.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.common.dto.DropdownResponseDto;
import com.pancaran.master.feature.tripplan.dto.response.ActivityResponseDto;
import com.pancaran.master.feature.tripplan.service.LocationCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/location-categories")
@RequiredArgsConstructor
public class LocationCategoryController {

    private final LocationCategoryService service;

    // Endpoint untuk mendapatkan daftar kategori lokasi (dipakai dropdown frontend)
    @GetMapping
    public ResponseEntity<APIResponse<List<DropdownResponseDto<String>>>> getLocationCategories() {
        return ResponseEntity.ok(APIResponse.success(service.getLocationCategories()));
    }

    // Endpoint untuk mendapatkan daftar aktivitas berdasarkan ID kategori lokasi
    @GetMapping("/{id}/activities")
    public ResponseEntity<APIResponse<List<ActivityResponseDto>>> getActivitiesByCategoryId(
            @PathVariable("id") String id) {
        return ResponseEntity.ok(APIResponse.success(service.getActivitiesByCategoryId(id)));
    }
}
