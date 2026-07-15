package com.pancaran.master.feature.tripplan.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@JsonPropertyOrder({"seqNo", "startSeqNo", "endSeqNo", "remarks", "units"})
public class RouteSegmentDto implements Serializable {
    @NotNull
    private Integer seqNo;
    @NotNull
    private Integer startSeqNo;
    @NotNull
    private Integer endSeqNo;
    private String remarks;
    private List<RouteSegmentUnit> units;

    @Getter
    @Setter
    public static class RouteSegmentUnit {
        private String unitTypeId;
        private String description;
    }
}
