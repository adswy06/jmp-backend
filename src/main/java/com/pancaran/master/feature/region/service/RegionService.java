package com.pancaran.master.feature.region.service;

import com.pancaran.master.common.dto.ScrollResponseDto;
import com.pancaran.master.feature.region.dto.*;
import com.pancaran.master.feature.region.repository.RegionJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "wilayah-dbTransactionManager", readOnly = true)
public class RegionService {

    private final RegionJdbcRepository jdbcRepository;

    // Direct JDBC-based methods are defined below:

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true)
    public List<RegionDropdownDto> getRegionDropdown(String search, int page, int size) {
        int offset = page * size;
        int remainingSize = size;
        List<RegionDropdownDto> results = new java.util.ArrayList<>();

        // 1. Provinces
        long countP = jdbcRepository.countProvinces(search);
        if (offset < countP) {
            int pLimit = (int) Math.min(remainingSize, countP - offset);
            List<Object[]> provinces = jdbcRepository.findProvincesPaginated(search, offset, pLimit);
            for (Object[] p : provinces) {
                results.add(new RegionDropdownDto((String) p[0], (String) p[1], "PROVINCE", null));
            }
            remainingSize -= pLimit;
            offset = 0;
        } else {
            offset -= countP;
        }

        if (remainingSize <= 0) return results;

        // 2. Regencies
        long countR = jdbcRepository.countRegencies(search);
        if (offset < countR) {
            int rLimit = (int) Math.min(remainingSize, countR - offset);
            List<Object[]> regencies = jdbcRepository.findRegenciesPaginated(search, offset, rLimit);
            for (Object[] r : regencies) {
                results.add(new RegionDropdownDto((String) r[0], (String) r[1], "REGENCY", (String) r[2]));
            }
            remainingSize -= rLimit;
            offset = 0;
        } else {
            offset -= countR;
        }

        if (remainingSize <= 0) return results;

        // 3. Districts
        long countD = jdbcRepository.countDistricts(search);
        if (offset < countD) {
            int dLimit = (int) Math.min(remainingSize, countD - offset);
            List<Object[]> districts = jdbcRepository.findDistrictsPaginated(search, offset, dLimit);
            for (Object[] d : districts) {
                results.add(new RegionDropdownDto((String) d[0], (String) d[1], "DISTRICT", (String) d[2]));
            }
            remainingSize -= dLimit;
            offset = 0;
        } else {
            offset -= countD;
        }

        if (remainingSize <= 0) return results;

        // 4. Villages
        long countV = jdbcRepository.countVillages(search);
        if (offset < countV) {
            int vLimit = (int) Math.min(remainingSize, countV - offset);
            List<Object[]> villages = jdbcRepository.findVillagesPaginated(search, offset, vLimit);
            for (Object[] v : villages) {
                results.add(new RegionDropdownDto((String) v[0], (String) v[1], "VILLAGE", (String) v[2]));
            }
        }

        return results;
    }

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true)
    public ScrollResponseDto<RegionTreeDto> getRegionTree(String code, String search, int page, int size) {
        List<Object[]> provinces = Collections.emptyList();
        List<Object[]> regencies = Collections.emptyList();
        List<Object[]> districts = Collections.emptyList();
        List<Object[]> villages = Collections.emptyList();

        if (search != null && !search.trim().isEmpty()) {
            // 1. Search matching nodes at all levels (wildcard search)
            List<Object[]> matchedProv = jdbcRepository.searchProvinces(search);
            List<Object[]> matchedKab = jdbcRepository.searchRegencies(search);
            List<Object[]> matchedKec = jdbcRepository.searchDistricts(search);
            List<Object[]> matchedDesa = jdbcRepository.searchVillages(search);

            // 2. Track all codes to load (build matched ancestor path sets)
            java.util.Set<String> provCodes = new java.util.HashSet<>();
            java.util.Set<String> kabCodes = new java.util.HashSet<>();
            java.util.Set<String> kecCodes = new java.util.HashSet<>();
            java.util.Set<String> desaCodes = new java.util.HashSet<>();

            for (Object[] p : matchedProv) {
                provCodes.add((String) p[0]);
            }
            for (Object[] k : matchedKab) {
                kabCodes.add((String) k[0]);
                provCodes.add((String) k[2]);
            }
            for (Object[] c : matchedKec) {
                kecCodes.add((String) c[0]);
                kabCodes.add((String) c[2]);
                provCodes.add(((String) c[2]).substring(0, 2));
            }
            for (Object[] v : matchedDesa) {
                desaCodes.add((String) v[0]);
                kecCodes.add((String) v[2]);
                kabCodes.add(((String) v[2]).substring(0, 4));
                provCodes.add(((String) v[2]).substring(0, 2));
            }            // 2.5 Descendant Expansion with Safety Limit Safeguard
            java.util.Set<String> directDesaCodes = matchedDesa.stream().map(v -> (String) v[0]).collect(Collectors.toSet());
            java.util.Set<String> directKecCodes = matchedKec.stream().map(c -> (String) c[0]).collect(Collectors.toSet());
            java.util.Set<String> directKabCodes = matchedKab.stream().map(k -> (String) k[0]).collect(Collectors.toSet());
            java.util.Set<String> directProvCodes = matchedProv.stream().map(p -> (String) p[0]).collect(Collectors.toSet());

            java.util.Set<String> provToExpand = new java.util.HashSet<>(directProvCodes);
            List<Object[]> regenciesUnderProvs = Collections.emptyList();
            if (!provToExpand.isEmpty()) {
                regenciesUnderProvs = jdbcRepository.findRegenciesByProvinceCodes(new java.util.ArrayList<>(provToExpand));
                for (Object[] r : regenciesUnderProvs) {
                    kabCodes.add((String) r[0]);
                }
            }

            java.util.Set<String> kabToExpand = new java.util.HashSet<>(directKabCodes);
            for (Object[] r : regenciesUnderProvs) {
                kabToExpand.add((String) r[0]);
            }
            List<Object[]> districtsUnderKabs = Collections.emptyList();
            if (!kabToExpand.isEmpty()) {
                districtsUnderKabs = jdbcRepository.findDistrictsByRegencyCodes(new java.util.ArrayList<>(kabToExpand));
                for (Object[] d : districtsUnderKabs) {
                    kecCodes.add((String) d[0]);
                }
            }

            java.util.Set<String> kecToExpand = new java.util.HashSet<>(directKecCodes);
            for (Object[] d : districtsUnderKabs) {
                kecToExpand.add((String) d[0]);
            }
            List<Object[]> villagesUnderKecs = Collections.emptyList();
            if (!kecToExpand.isEmpty()) {
                villagesUnderKecs = jdbcRepository.findVillagesByDistrictCodes(new java.util.ArrayList<>(kecToExpand));
                for (Object[] v : villagesUnderKecs) {
                    desaCodes.add((String) v[0]);
                }
            }

            // Apply 500 nodes safety limit check
            if (desaCodes.size() > 500) {
                desaCodes = new java.util.HashSet<>(directDesaCodes);
                kecCodes.clear();
                kabCodes.clear();
                provCodes.clear();

                for (Object[] p : matchedProv) {
                    provCodes.add((String) p[0]);
                }
                for (Object[] k : matchedKab) {
                    kabCodes.add((String) k[0]);
                    provCodes.add((String) k[2]);
                }
                for (Object[] c : matchedKec) {
                    kecCodes.add((String) c[0]);
                    kabCodes.add((String) c[2]);
                    provCodes.add(((String) c[2]).substring(0, 2));
                }
                for (Object[] v : matchedDesa) {
                    kecCodes.add((String) v[2]);
                    kabCodes.add(((String) v[2]).substring(0, 4));
                    provCodes.add(((String) v[2]).substring(0, 2));
                }
            }

            // 3. Load only the matched nodes using optimized database queries
            if (!provCodes.isEmpty()) {
                provinces = jdbcRepository.findProvinces(null).stream()
                        .filter(p -> provCodes.contains(p[0]))
                        .collect(Collectors.toList());
            }
            if (!kabCodes.isEmpty()) {
                regencies = jdbcRepository.findRegenciesByCodes(new java.util.ArrayList<>(kabCodes));
            }
            if (!kecCodes.isEmpty()) {
                districts = jdbcRepository.findDistrictsByCodes(new java.util.ArrayList<>(kecCodes));
            }
            if (!desaCodes.isEmpty()) {
                villages = jdbcRepository.findVillagesByCodes(new java.util.ArrayList<>(desaCodes));
            }
        } else if (code != null && !code.trim().isEmpty()) {
            // Lazy load child nodes under a specific code!
            String cleanCode = code.trim();
            List<RegionTreeDto> items = Collections.emptyList();
            if (cleanCode.length() == 2) {
                // Return only Regencies under this Province (mapped as parent-child tree)
                regencies = jdbcRepository.findRegencies(cleanCode, null);
                // Return them as root nodes for the response
                items = regencies.stream()
                        .map(r -> RegionTreeDto.builder()
                                .code((String) r[0])
                                .name((String) r[1])
                                .areaKm2((Double) r[3])
                                .lat((Double) r[4])
                                .lng((Double) r[5])
                                .districs(new java.util.ArrayList<>())
                                .build())
                        .collect(Collectors.toList());
            } else if (cleanCode.length() == 4) {
                // Return only Districts under this Regency
                districts = jdbcRepository.findDistrictsByRegencyCodes(List.of(cleanCode));
                items = districts.stream()
                        .map(d -> RegionTreeDto.builder()
                                .code((String) d[0])
                                .name((String) d[1])
                                .areaKm2((Double) d[3])
                                .lat((Double) d[4])
                                .lng((Double) d[5])
                                .vilages(new java.util.ArrayList<>())
                                .build())
                        .collect(Collectors.toList());
            } else if (cleanCode.length() == 6) {
                // Return only Villages under this District
                villages = jdbcRepository.findVillagesByDistrictCodes(List.of(cleanCode));
                items = villages.stream()
                        .map(v -> RegionTreeDto.builder()
                                .code((String) v[0])
                                .name((String) v[1])
                                .areaKm2((Double) v[3])
                                .lat((Double) v[4])
                                .lng((Double) v[5])
                                .build())
                        .collect(Collectors.toList());
            }
            return new ScrollResponseDto<>(items, false);
        } else {
            // Default load Provinces paginated (lazy load root)
            int offset = page * size;
            provinces = jdbcRepository.findProvincesPaginated(null, offset, size + 1);
            boolean hasNext = provinces.size() > size;
            if (hasNext) {
                provinces = provinces.subList(0, size);
            }
            List<RegionTreeDto> items = provinces.stream()
                    .map(p -> RegionTreeDto.builder()
                            .code((String) p[0])
                            .name((String) p[1])
                            .areaKm2((Double) p[2])
                            .lat((Double) p[3])
                            .lng((Double) p[4])
                            .regency(new java.util.ArrayList<>())
                            .build())
                    .collect(Collectors.toList());
            return new ScrollResponseDto<>(items, hasNext);
        }

        // Build tree for search results
        Map<String, RegionTreeDto> villageMap = villages.stream().collect(Collectors.toMap(
            v -> (String) v[0],
            v -> RegionTreeDto.builder()
                .code((String) v[0])
                .name((String) v[1])
                .areaKm2((Double) v[3])
                .lat((Double) v[4])
                .lng((Double) v[5])
                .build()
        ));

        Map<String, RegionTreeDto> districtMap = districts.stream().collect(Collectors.toMap(
            d -> (String) d[0],
            d -> RegionTreeDto.builder()
                .code((String) d[0])
                .name((String) d[1])
                .areaKm2((Double) d[3])
                .lat((Double) d[4])
                .lng((Double) d[5])
                .vilages(new java.util.ArrayList<>())
                .build()
        ));

        Map<String, RegionTreeDto> regencyMap = regencies.stream().collect(Collectors.toMap(
            r -> (String) r[0],
            r -> RegionTreeDto.builder()
                .code((String) r[0])
                .name((String) r[1])
                .areaKm2((Double) r[3])
                .lat((Double) r[4])
                .lng((Double) r[5])
                .districs(new java.util.ArrayList<>())
                .build()
        ));

        Map<String, RegionTreeDto> provinceMap = provinces.stream().collect(Collectors.toMap(
            p -> (String) p[0],
            p -> RegionTreeDto.builder()
                .code((String) p[0])
                .name((String) p[1])
                .areaKm2((Double) p[2])
                .lat((Double) p[3])
                .lng((Double) p[4])
                .regency(new java.util.ArrayList<>())
                .build()
        ));

        for (Object[] v : villages) {
            RegionTreeDto vDto = villageMap.get((String) v[0]);
            RegionTreeDto dDto = districtMap.get((String) v[2]);
            if (dDto != null && vDto != null) {
                dDto.getVilages().add(vDto);
            }
        }

        for (Object[] d : districts) {
            RegionTreeDto dDto = districtMap.get((String) d[0]);
            RegionTreeDto rDto = regencyMap.get((String) d[2]);
            if (rDto != null && dDto != null) {
                rDto.getDistrics().add(dDto);
            }
        }

        for (Object[] r : regencies) {
            RegionTreeDto rDto = regencyMap.get((String) r[0]);
            RegionTreeDto pDto = provinceMap.get((String) r[2]);
            if (pDto != null && rDto != null) {
                pDto.getRegency().add(rDto);
            }
        }

        List<RegionTreeDto> items = provinces.stream()
                .map(p -> provinceMap.get((String) p[0]))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
        return new ScrollResponseDto<>(items, false);
    }

    public String getRegionGeom(String code) {
        if (code == null) return null;
        if (code.length() == 10) {
            return jdbcRepository.findVillageGeom(code);
        } else if (code.length() == 6) {
            return jdbcRepository.findDistrictGeom(code);
        } else if (code.length() == 4) {
            return jdbcRepository.findRegencyGeom(code);
        } else if (code.length() == 2) {
            return jdbcRepository.findProvinceGeom(code);
        }
        return null;
    }

    @Transactional(value = "wilayah-dbTransactionManager", readOnly = true)
    public ScrollResponseDto<com.pancaran.master.common.dto.DropdownResponseDto<String>> getRegionDropdownCommon(String search, int page, int size) {
        List<RegionDropdownDto> list = getRegionDropdown(search, page, size + 1);
        boolean hasNext = list.size() > size;
        if (hasNext) {
            list = list.subList(0, size);
        }
        List<com.pancaran.master.common.dto.DropdownResponseDto<String>> items = list.stream()
                .map(item -> new com.pancaran.master.common.dto.DropdownResponseDto<>(
                        item.getCode(),
                        item.getName() + " (" + item.getType() + ")"
                ))
                .collect(Collectors.toList());
        return new ScrollResponseDto<>(items, hasNext);
    }
}
