package com.pancaran.master.feature.tripplan.service;

import com.apik.core.common.helper.CoreUtil;
import com.pancaran.master.common.ApiException;
import com.pancaran.master.feature.tripplan.dto.request.RoadHazardCreateDto;
import com.pancaran.master.feature.tripplan.dto.request.RoadHazardUpdateDto;
import com.pancaran.master.feature.tripplan.entity.master.RoadHazardEntity;
import com.pancaran.master.feature.tripplan.repository.RoadHazardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "jmp-dbTransactionManager")
public class RoadHazardService {

    private final RoadHazardRepository repository;

    public RoadHazardEntity createHazard(RoadHazardCreateDto dto) {
        RoadHazardEntity entity = new RoadHazardEntity();
        entity.setId(CoreUtil.createUUID());
        entity.setName(dto.getName());
        entity.setHazardType(dto.getHazardType());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setRadius(dto.getRadius() != null ? dto.getRadius() : 50);
        entity.setSeverity(dto.getSeverity() != null ? dto.getSeverity() : "MEDIUM");
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);

        return repository.save(entity, true);
    }

    @Transactional(value = "jmp-dbTransactionManager", readOnly = true)
    public List<RoadHazardEntity> getAllHazards(String name, String hazardType, Boolean isActive) {
        return repository.findAll(name, hazardType, isActive);
    }

    @Transactional(value = "jmp-dbTransactionManager", readOnly = true)
    public RoadHazardEntity getHazardById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApiException(404, "Road hazard not found with id: " + id));
    }

    public RoadHazardEntity updateHazard(String id, RoadHazardUpdateDto dto) {
        RoadHazardEntity entity = getHazardById(id);

        if (dto.getName() != null) {
            entity.setName(dto.getName());
        }
        if (dto.getHazardType() != null) {
            entity.setHazardType(dto.getHazardType());
        }
        if (dto.getLatitude() != null) {
            entity.setLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            entity.setLongitude(dto.getLongitude());
        }
        if (dto.getRadius() != null) {
            entity.setRadius(dto.getRadius());
        }
        if (dto.getSeverity() != null) {
            entity.setSeverity(dto.getSeverity());
        }
        if (dto.getActive() != null) {
            entity.setActive(dto.getActive());
        }

        return repository.save(entity, false);
    }

    public void deleteHazard(String id) {
        RoadHazardEntity entity = getHazardById(id);
        repository.delete(entity);
    }
}
