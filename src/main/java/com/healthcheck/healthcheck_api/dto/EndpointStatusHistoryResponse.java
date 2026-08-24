package com.healthcheck.healthcheck_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EndpointStatusHistoryResponse {

    private Long id;

    private LocalDateTime checkedAt;

    private String status;

    private Integer statusCode;

    private String error;

    private Long responseTimeMs;
}