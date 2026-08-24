package com.healthcheck.healthcheck_api.services;

import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.models.EndpointStatusHistory;
import com.healthcheck.healthcheck_api.repositories.EndpointRepository;
import com.healthcheck.healthcheck_api.repositories.EndpointStatusHistoryRepository;
import io.restassured.response.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;

@Service
public class HealthCheckService {

    private final EndpointRepository endpointRepository;
    private final EndpointStatusHistoryRepository historyRepository;

    public HealthCheckService(EndpointRepository endpointRepository ,  EndpointStatusHistoryRepository historyRepository) {
        this.endpointRepository = endpointRepository;
        this.historyRepository = historyRepository;
    }

    public void checkEndpoint(Endpoint endpoint) {

        LocalDateTime checkedAt = LocalDateTime.now();
        long startTime = System.currentTimeMillis();

        EndpointStatusHistory history = new EndpointStatusHistory();
        history.setEndpoint(endpoint);
        history.setCheckedAt(checkedAt);

        try {

            Response response = given()
                    .when()
                    .get(endpoint.getUrl());

            long responseTime =
                    System.currentTimeMillis() - startTime;

            int statusCode = response.getStatusCode();

            endpoint.setLastCheckedAt(checkedAt);
            endpoint.setLastStatusCode(statusCode);
            endpoint.setLastError(null);

            history.setStatusCode(statusCode);
            history.setResponseTimeMs(responseTime);

            if (statusCode >= 200 && statusCode < 300) {

                endpoint.setStatus("UP");
                history.setStatus("UP");
                history.setError(null);

            } else {

                endpoint.setStatus("DOWN");

                String error =
                        "HTTP request returned status code " + statusCode;

                endpoint.setLastError(error);

                history.setStatus("DOWN");
                history.setError(error);
            }

        } catch (Exception exception) {

            long responseTime =
                    System.currentTimeMillis() - startTime;

            endpoint.setLastCheckedAt(checkedAt);
            endpoint.setLastStatusCode(null);
            endpoint.setStatus("DOWN");
            endpoint.setLastError(exception.getMessage());

            history.setStatus("DOWN");
            history.setStatusCode(null);
            history.setError(exception.getMessage());
            history.setResponseTimeMs(responseTime);
        }

        endpoint.setNextCheckAt(
                checkedAt.plusMinutes(
                        endpoint.getCheckIntervalMinutes()
                )
        );

        endpointRepository.save(endpoint);
        historyRepository.save(history);
    }
}