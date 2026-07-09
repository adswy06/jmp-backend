package com.pancaran.master.feature.jmp.dto;

import lombok.Data;
import java.util.List;

@Data
public class JmpRequestDto {
    private String id;
    private String customerId;
    private String consigneeId;
    private String commercialRoute;
    private String referenceNo;
    private String title;
    private String description;
    private String status;
    private Boolean saveToMaster;
    private Boolean isNotificationGlobal;
    private List<JmpTripPlanRequestDto.UnitRequestDto> units;

    private List<JmpTripPlanRequestDto> tripPlans;
}
