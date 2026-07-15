package com.pancaran.master.feature.jmp.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import java.util.List;

@Data
@JsonPropertyOrder({
    "id", "customerId", "consigneeId", "commercialRoute", "referenceNo",
    "title", "description", "status", "saveToMaster", "isNotificationGlobal",
    "units", "tripPlans"
})
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
