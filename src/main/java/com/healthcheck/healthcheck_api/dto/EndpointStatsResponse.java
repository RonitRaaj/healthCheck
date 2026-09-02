package com.healthcheck.healthcheck_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointStatsResponse {

    private Long endpointId;
    private double uptime;
    private double downtime;
    private long checks;
    private double avgResponseTime;
    private long failures;
}