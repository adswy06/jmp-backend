package com.pancaran.master.feature.region.service;

import com.pancaran.master.feature.region.dto.*;
import com.pancaran.master.feature.region.entity.*;
import com.pancaran.master.feature.region.repository.RegionRepository;
import com.pancaran.master.feature.region.repository.RegionJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "wilayah-dbTransactionManager", readOnly = true)
public class RegionService {

    private final RegionRepository repository;
    private final RegionJdbcRepository jdbcRepository;

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true, timeout = 5)
    public List<ProvinceDto> getProvinces(String name, boolean includeGeom) {
        List<ProvinceEntity> entities = repository.findProvinces(name);
        Map<String, String> geomMap = Collections.emptyMap();
        if (includeGeom && !entities.isEmpty()) {
            List<String> codes = entities.stream().map(ProvinceEntity::getKodeProv).collect(Collectors.toList());
            geomMap = jdbcRepository.findProvinceGeoms(codes);
        }
        final Map<String, String> finalGeomMap = geomMap;
        return entities.stream()
                .map(entity -> toProvinceDto(entity, finalGeomMap.get(entity.getKodeProv())))
                .collect(Collectors.toList());
    }

    public Optional<ProvinceDto> getProvinceByCode(String code, boolean includeGeom) {
        return repository.findProvinceByCode(code).map(entity -> {
            String geomJson = includeGeom ? jdbcRepository.findProvinceGeom(code) : null;
            return toProvinceDto(entity, geomJson);
        });
    }

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true, timeout = 5)
    public List<RegencyDto> getRegencies(String provinceCode, String name, boolean includeGeom) {
        List<RegencyEntity> entities = repository.findRegencies(provinceCode, name);
        Map<String, String> geomMap = Collections.emptyMap();
        if (includeGeom && !entities.isEmpty()) {
            List<String> codes = entities.stream().map(RegencyEntity::getKodeKab).collect(Collectors.toList());
            geomMap = jdbcRepository.findRegencyGeoms(codes);
        }
        final Map<String, String> finalGeomMap = geomMap;
        return entities.stream()
                .map(entity -> toRegencyDto(entity, finalGeomMap.get(entity.getKodeKab())))
                .collect(Collectors.toList());
    }

    public Optional<RegencyDto> getRegencyByCode(String code, boolean includeGeom) {
        return repository.findRegencyByCode(code).map(entity -> {
            String geomJson = includeGeom ? jdbcRepository.findRegencyGeom(code) : null;
            return toRegencyDto(entity, geomJson);
        });
    }

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true, timeout = 10)
    public List<DistrictDto> getDistricts(String regencyCode, String name, boolean includeGeom) {
        List<DistrictEntity> entities = repository.findDistricts(regencyCode, name);
        Map<String, String> geomMap = Collections.emptyMap();
        if (includeGeom && !entities.isEmpty()) {
            List<String> codes = entities.stream().map(DistrictEntity::getKodeKec).collect(Collectors.toList());
            geomMap = jdbcRepository.findDistrictGeoms(codes);
        }
        final Map<String, String> finalGeomMap = geomMap;
        return entities.stream()
                .map(entity -> toDistrictDto(entity, finalGeomMap.get(entity.getKodeKec())))
                .collect(Collectors.toList());
    }

    public Optional<DistrictDto> getDistrictByCode(String code, boolean includeGeom) {
        return repository.findDistrictByCode(code).map(entity -> {
            String geomJson = includeGeom ? jdbcRepository.findDistrictGeom(code) : null;
            return toDistrictDto(entity, geomJson);
        });
    }

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true, timeout = 15)
    public List<VillageDto> getVillages(String districtCode, String name, boolean includeGeom) {
        List<VillageEntity> entities = repository.findVillages(districtCode, name);
        Map<String, String> geomMap = Collections.emptyMap();
        if (includeGeom && !entities.isEmpty()) {
            List<String> codes = entities.stream().map(VillageEntity::getKodeDesa).collect(Collectors.toList());
            geomMap = jdbcRepository.findVillageGeoms(codes);
        }
        final Map<String, String> finalGeomMap = geomMap;
        return entities.stream()
                .map(entity -> toVillageDto(entity, finalGeomMap.get(entity.getKodeDesa())))
                .collect(Collectors.toList());
    }

    public Optional<VillageDto> getVillageByCode(String code, boolean includeGeom) {
        return repository.findVillageByCode(code).map(entity -> {
            String geomJson = includeGeom ? jdbcRepository.findVillageGeom(code) : null;
            return toVillageDto(entity, geomJson);
        });
    }

    public List<PostalCodeDto> getPostalCodes(String villageCode, String code) {
        return repository.findPostalCodes(villageCode, code).stream()
                .map(this::toPostalCodeDto)
                .collect(Collectors.toList());
    }

    private ProvinceDto toProvinceDto(ProvinceEntity entity, String geomJson) {
        if (entity == null) return null;
        return ProvinceDto.builder()
                .id(entity.getId())
                .code(entity.getKodeProv())
                .name(entity.getNamaProvinsi())
                .areaKm2(entity.getAreaKm2())
                .jumlahPenduduk(entity.getJumlahPenduduk())
                .jumlahKk(entity.getJumlahKk())
                .jumlahKab(entity.getJumlahKab())
                .jumlahKota(entity.getJumlahKota())
                .jumlahKec(entity.getJumlahKec())
                .jumlahDesa(entity.getJumlahDesa())
                .jumlahKel(entity.getJumlahKel())
                .kepadatan(entity.getKepadatan())
                .luasWilayah(entity.getLuasWilayah())
                .geom(geomJson)
                .build();
    }

    private RegencyDto toRegencyDto(RegencyEntity entity, String geomJson) {
        if (entity == null) return null;
        return RegencyDto.builder()
                .id(entity.getId())
                .code(entity.getKodeKab())
                .provinceCode(entity.getKodeProv())
                .name(entity.getNamaKabupaten())
                .tipe(entity.getTipe())
                .areaKm2(entity.getAreaKm2())
                .jumlahPenduduk(entity.getJumlahPenduduk())
                .jumlahKk(entity.getJumlahKk())
                .jumlahKec(entity.getJumlahKec())
                .jumlahDesa(entity.getJumlahDesa())
                .jumlahKel(entity.getJumlahKel())
                .kepadatan(entity.getKepadatan())
                .luasWilayah(entity.getLuasWilayah())
                .geom(geomJson)
                .build();
    }

    private DistrictDto toDistrictDto(DistrictEntity entity, String geomJson) {
        if (entity == null) return null;
        return DistrictDto.builder()
                .id(entity.getId())
                .code(entity.getKodeKec())
                .regencyCode(entity.getKodeKab())
                .name(entity.getNamaKecamatan())
                .areaKm2(entity.getAreaKm2())
                .jumlahPenduduk(entity.getJumlahPenduduk())
                .jumlahKk(entity.getJumlahKk())
                .jumlahDesa(entity.getJumlahDesa())
                .jumlahKel(entity.getJumlahKel())
                .kepadatan(entity.getKepadatan())
                .luasWilayah(entity.getLuasWilayah())
                .geom(geomJson)
                .build();
    }

    private VillageDto toVillageDto(VillageEntity entity, String geomJson) {
        if (entity == null) return null;
        return VillageDto.builder()
                .id(entity.getId())
                .code(entity.getKodeDesa())
                .districtCode(entity.getKodeKec())
                .name(entity.getNamaDesa())
                .tipe(entity.getTipe())
                .areaKm2(entity.getAreaKm2())
                .jumlahPenduduk(entity.getJumlahPenduduk())
                .pulau(entity.getPulau())
                .jangkauan(entity.getJangkauan())
                .geom(geomJson)
                .build();
    }

    private PostalCodeDto toPostalCodeDto(PostalCodeEntity entity) {
        if (entity == null) return null;
        return PostalCodeDto.builder()
                .id(entity.getId())
                .villageCode(entity.getKodeDesa())
                .postalCode(entity.getKodePos())
                .status(entity.getStatus())
                .confidence(entity.getConfidence())
                .sumber(entity.getSumber())
                .build();
    }
}
