package com.pancaran.master.feature.tripplan.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@JsonPropertyOrder({"id", "value", "activities"})
public class LocationCategoryWithActivitiesDto implements Serializable {
    private String id;
    private String value;
    private List<ActivityResponseDto> activities;
}
