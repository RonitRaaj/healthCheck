package com.healthcheck.healthcheck_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_status_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EndpointStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoint_id", nullable = false)
    private Endpoint endpoint;

    @Column(nullable = false)
    private LocalDateTime checkedAt;

    @Column(nullable = false)
    private String status;

    private Integer statusCode;

    private String error;

    private Long responseTimeMs;
}