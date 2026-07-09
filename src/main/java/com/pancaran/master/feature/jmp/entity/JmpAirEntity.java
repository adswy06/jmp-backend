package com.pancaran.master.feature.jmp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_jmp_air")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class JmpAirEntity {
    @Id
    private String id;

    @Column(name = "jmp_trip_plan_id", nullable = false)
    private String jmpTripPlanId;

    @Column(name = "origin_airport_id")
    private String originAirportId;

    @Column(name = "destination_airport_id")
    private String destinationAirportId;

    private String airline;

    @Column(name = "flight_no")
    private String flightNo;

    private LocalDateTime etd;

    private LocalDateTime eta;
}
