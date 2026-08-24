package com.healthcheck.healthcheck_api.scheduler;

import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.repositories.EndpointRepository;
import com.healthcheck.healthcheck_api.services.HealthCheckService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
public class HealthCheckScheduler {

    private final EndpointRepository endpointRepository;
    private final HealthCheckService healthCheckService;
    private final ExecutorService healthCheckExecutor;

    public HealthCheckScheduler(EndpointRepository endpointRepository , HealthCheckService healthCheckService ,  ExecutorService healthCheckExecutor) {
        this.endpointRepository = endpointRepository;
        this.healthCheckService = healthCheckService;
        this.healthCheckExecutor = healthCheckExecutor;
    }

    @Scheduled(fixedRate = 60_000)
    public void runHealthChecks() {

        LocalDateTime now = LocalDateTime.now();

        List<Endpoint> dueEndpoints =
                endpointRepository
                        .findByActiveTrueAndNextCheckAtLessThanEqual(now);

        System.out.println(
                "Found " + dueEndpoints.size()
                        + " endpoints due for health check"
        );

        for (Endpoint endpoint : dueEndpoints) {
            healthCheckExecutor.submit(() -> {

                System.out.println(
                        "Checking " + endpoint.getName()
                                + " on thread "
                                + Thread.currentThread().getName()
                );

                healthCheckService.checkEndpoint(endpoint);
            });
        }
    }
}