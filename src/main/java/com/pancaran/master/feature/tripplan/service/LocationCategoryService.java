package com.pancaran.master.feature.tripplan.service;

import com.pancaran.master.common.ApiException;
import com.pancaran.master.common.dto.DropdownResponseDto;
import com.pancaran.master.feature.tripplan.dto.response.ActivityResponseDto;
import com.pancaran.master.feature.tripplan.dto.response.LocationCategoryWithActivitiesDto;
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

    public List<LocationCategoryWithActivitiesDto> getLocationCategoriesWithActivities(String categoryId) {
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            LocationCategoryEntity entity = repository.findCategoryByIdWithActivities(categoryId)
                    .orElseThrow(() -> new ApiException(404, "Location category not found with id: " + categoryId));
            return List.of(toCategoryWithActivitiesDto(entity));
        }

        return repository.findAllCategoriesWithActivities().stream()
                .map(this::toCategoryWithActivitiesDto)
                .collect(Collectors.toList());
    }

    public List<ActivityResponseDto> getActivities(String categoryId, String name) {
        return repository.findActivities(categoryId, name).stream()
                .map(this::toActivityResponseDto)
                .collect(Collectors.toList());
    }

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

    private LocationCategoryWithActivitiesDto toCategoryWithActivitiesDto(LocationCategoryEntity entity) {
        if (entity == null) return null;

        List<ActivityResponseDto> activities = entity.getActivities() == null ? java.util.Collections.emptyList() :
                entity.getActivities().stream()
                        .map(this::toActivityResponseDto)
                        .sorted(java.util.Comparator.comparing(ActivityResponseDto::getName, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                        .collect(Collectors.toList());

        return LocationCategoryWithActivitiesDto.builder()
                .id(entity.getId())
                .value(entity.getCategoryName())
                .activities(activities)
                .build();
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
                .name(entity.getName())
                .cost(entity.getCost())
                .leadTime(entity.getLeadTime())
                .build();
    }
}
