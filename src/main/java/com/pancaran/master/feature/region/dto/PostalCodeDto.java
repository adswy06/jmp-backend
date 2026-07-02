package com.pancaran.master.feature.region.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PostalCodeDto {
    private Integer id;
    private String villageCode;
    private String postalCode;
    private String status;
    private BigDecimal confidence;
    private String sumber;
}
