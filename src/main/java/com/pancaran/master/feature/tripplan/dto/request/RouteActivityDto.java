package com.pancaran.master.feature.tripplan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RouteActivityDto implements Serializable {
    @NotBlank
    private String activityId;
    private Integer leadTime;
    private Double amount;
    private List<RouteActivityExtraCost> extraCost;

    @Getter
    @Setter
    public static class RouteActivityExtraCost {
        private String name;
        private Double amount;
    }
}

