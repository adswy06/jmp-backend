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
@CrossOrigin(origins = "*")
@Tag(name = "Regions (Wilayah Indonesia)", description = "Endpoints for querying Indonesian administrative divisions and PostGIS geometries")
public class RegionController {

    private final RegionService service;

    @GetMapping("/provinces")
    @Operation(summary = "Get Provinces List", description = "Retrieves all provinces in Indonesia with optional name filter and boundary geometries.")
    public ResponseEntity<APIResponse<List<ProvinceDto>>> getProvinces(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        return ResponseEntity.ok(APIResponse.success(service.getProvinces(name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/provinces/{code}")
    @Operation(summary = "Get Province by Code", description = "Retrieves details of a single province by its 2-digit code.")
    public ResponseEntity<APIResponse<ProvinceDto>> getProvinceByCode(
            @PathVariable("code") String code,
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        ProvinceDto dto = service.getProvinceByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Province not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/regencies")
    @Operation(summary = "Get Regencies List", description = "Retrieves regencies (Kabupaten/Kota) with parent province and name filters.")
    public ResponseEntity<APIResponse<List<RegencyDto>>> getRegencies(
            @RequestParam(value = "province_code", required = false) String provinceCode,
            @RequestParam(value = "provinceCode", required = false) String provinceCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalProvinceCode = provinceCode != null ? provinceCode : provinceCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getRegencies(finalProvinceCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/regencies/{code}")
    @Operation(summary = "Get Regency by Code", description = "Retrieves details of a single regency by its 4-digit code.")
    public ResponseEntity<APIResponse<RegencyDto>> getRegencyByCode(
            @PathVariable("code") String code,
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        RegencyDto dto = service.getRegencyByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Regency not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/districts")
    @Operation(summary = "Get Districts List", description = "Retrieves districts (Kecamatan) with parent regency and name filters.")
    public ResponseEntity<APIResponse<List<DistrictDto>>> getDistricts(
            @RequestParam(value = "regency_code", required = false) String regencyCode,
            @RequestParam(value = "regencyCode", required = false) String regencyCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalRegencyCode = regencyCode != null ? regencyCode : regencyCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getDistricts(finalRegencyCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/districts/{code}")
    @Operation(summary = "Get District by Code", description = "Retrieves details of a single district by its 6-digit code.")
    public ResponseEntity<APIResponse<DistrictDto>> getDistrictByCode(
            @PathVariable("code") String code,
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        DistrictDto dto = service.getDistrictByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "District not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/villages")
    @Operation(summary = "Get Villages List", description = "Retrieves villages (Desa/Kelurahan) with parent district and name filters.")
    public ResponseEntity<APIResponse<List<VillageDto>>> getVillages(
            @RequestParam(value = "district_code", required = false) String districtCode,
            @RequestParam(value = "districtCode", required = false) String districtCodeAlt,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "include_geom", required = false, defaultValue = "false") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "false") boolean includeGeomAlt) {
        String finalDistrictCode = districtCode != null ? districtCode : districtCodeAlt;
        return ResponseEntity.ok(APIResponse.success(service.getVillages(finalDistrictCode, name, includeGeom || includeGeomAlt)));
    }

    @GetMapping("/villages/{code}")
    @Operation(summary = "Get Village by Code", description = "Retrieves details of a single village by its 10-digit code.")
    public ResponseEntity<APIResponse<VillageDto>> getVillageByCode(
            @PathVariable("code") String code,
            @RequestParam(value = "include_geom", required = false, defaultValue = "true") boolean includeGeom,
            @RequestParam(value = "includeGeom", required = false, defaultValue = "true") boolean includeGeomAlt) {
        VillageDto dto = service.getVillageByCode(code, includeGeom || includeGeomAlt)
                .orElseThrow(() -> new ApiException(404, "Village not found with code: " + code));
        return ResponseEntity.ok(APIResponse.success(dto));
    }

    @GetMapping("/postal-codes")
    @Operation(summary = "Get Postal Codes List", description = "Retrieves postal codes filtered by village code or postal code value.")
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
