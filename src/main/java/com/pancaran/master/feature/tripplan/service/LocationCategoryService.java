package com.pancaran.master.feature.tripplan.service;

import com.pancaran.master.common.ApiException;
import com.pancaran.master.common.dto.DropdownResponseDto;
import com.pancaran.master.feature.tripplan.dto.response.ActivityResponseDto;
import com.pancaran.master.feature.tripplan.entity.master.ActivityEntity;
import com.pancaran.master.feature.tripplan.entity.master.LocationCategoryEntity;
import com.pancaran.master.feature.tripplan.repository.LocationCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(value = "jmp-dbTransactionManager", readOnly = true)
public class LocationCategoryService {

    private final LocationCategoryRepository repository;

    public List<DropdownResponseDto<String>> getLocationCategories() {
        return repository.findAllCategories().stream()
                .map(this::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    public List<ActivityResponseDto> getActivitiesByCategoryId(String categoryId) {
        // Verifikasi bahwa kategori lokasi ada
        repository.findCategoryById(categoryId)
                .orElseThrow(() -> new ApiException(404, "Location category not found with id: " + categoryId));

        return repository.findActivitiesByCategoryId(categoryId).stream()
                .map(this::toActivityResponseDto)
                .collect(Collectors.toList());
    }

    private DropdownResponseDto<String> toCategoryResponseDto(LocationCategoryEntity entity) {
        if (entity == null) return null;
        return DropdownResponseDto.<String>builder()
                .id(entity.getId())
                .value(entity.getCategoryName())
                .build();
    }

    private ActivityResponseDto toActivityResponseDto(ActivityEntity entity) {
        if (entity == null) return null;
        return ActivityResponseDto.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .name(entity.getName())
                .build();
    }
}
