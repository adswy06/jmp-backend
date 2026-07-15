package com.pancaran.master.feature.tripplan.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ActivityResponseDto {
    private String id;
    private String name;
    private BigDecimal cost;
    private Integer leadTime;
}
