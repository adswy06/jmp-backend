package com.pancaran.master.feature.region.api;

import com.pancaran.master.common.APIResponse;
import com.pancaran.master.common.ApiException;
import com.pancaran.master.feature.region.dto.*;
import com.pancaran.master.feature.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService service;

    @GetMapping("/provinces")
    public ResponseEntity<APIResponse<List<ProvinceDto>>> getProvinces(
            @RequestParam(value = "name", required = false) String name,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        return ResponseEntity.ok(APIResponse.success(service.getProvinces(name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/provinces/{code}")
    public ResponseEntity<APIResponse<ProvinceDto>> getProvinceByCode(
            @PathVariable("code") String code,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        ProvinceDto dto = service.getProvinceByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Province not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/regencies")
    public ResponseEntity<APIResponse<List<RegencyDto>>> getRegencies(
            @RequestParam(value = "province_code", required = false) String provinceCode,
            @RequestParam(value = "provinceCode", required = false) String provinceCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalProvinceCode = provinceCode != null ? provinceCode : provinceCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getRegencies(finalProvinceCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/regencies/{code}")
    public ResponseEntity<APIResponse<RegencyDto>> getRegencyByCode(
            @PathVariable("code") String code,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        RegencyDto dto = service.getRegencyByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Regency not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/districts")
    public ResponseEntity<APIResponse<List<DistrictDto>>> getDistricts(
            @RequestParam(value = "regency_code", required = false) String regencyCode,
            @RequestParam(value = "regencyCode", required = false) String regencyCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalRegencyCode = regencyCode != null ? regencyCode : regencyCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getDistricts(finalRegencyCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/districts/{code}")
    public ResponseEntity<APIResponse<DistrictDto>> getDistrictByCode(
            @PathVariable("code") String code,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        DistrictDto dto = service.getDistrictByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "District not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/villages")
    public ResponseEntity<APIResponse<List<VillageDto>>> getVillages(
            @RequestParam(value = "district_code", required = false) String districtCode,
            @RequestParam(value = "districtCode", required = false) String districtCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalDistrictCode = districtCode != null ? districtCode : districtCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getVillages(finalDistrictCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/villages/{code}")
    public ResponseEntity<APIResponse<VillageDto>> getVillageByCode(
            @PathVariable("code") String code,
            // Mendukung format snake_case (include_geom) dari format wilayah-id
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            // Mendukung format camelCase (includeGeom) standar Java
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        VillageDto dto = service.getVillageByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Village not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/postal-codes")
    public ResponseEntity<APIResponse<List<PostalCodeDto>>> getPostalCodes(
            @RequestParam(value = "village_code", required = false) String villageCode,
            @RequestParam(value = "villageCode", required = false) String villageCodeAlt,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "postal_code", required = false) String postalCode,
            @RequestParam(value = "postalCode", required = false) String postalCodeAlt) {
        String finalVillageCode = villageCode != null ? villageCode : villageCodeAlt;
        String finalCode = code != null ? code : (postalCode != null ? postalCode : postalCodeAlt);
        return ResponseEntity.ok(APIResponse.success(service.getPostalCodes(finalVillageCode, finalCode)));
    }
}
