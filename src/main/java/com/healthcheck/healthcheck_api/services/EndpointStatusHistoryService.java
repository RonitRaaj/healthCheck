package com.healthcheck.healthcheck_api.services;

import com.healthcheck.healthcheck_api.dto.EndpointStatsResponse;
import com.healthcheck.healthcheck_api.dto.EndpointStatusHistoryResponse;
import com.healthcheck.healthcheck_api.models.EndpointStatusHistory;
import com.healthcheck.healthcheck_api.repositories.EndpointRepository;
import com.healthcheck.healthcheck_api.repositories.EndpointStatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EndpointStatusHistoryService {

    private final EndpointStatusHistoryRepository historyRepository;
    private final EndpointRepository endpointRepository;

    public EndpointStatusHistoryService(
            EndpointStatusHistoryRepository historyRepository,
            EndpointRepository endpointRepository) {

        this.historyRepository = historyRepository;
        this.endpointRepository = endpointRepository;
    }

    public List<EndpointStatusHistoryResponse> getHistory(Long endpointId) {

        if (!endpointRepository.existsById(endpointId)) {
            throw new RuntimeException("Endpoint not found");
        }

        return historyRepository
                .findByEndpointIdOrderByCheckedAtDesc(endpointId)
                .stream()
                .map(history -> {

                    EndpointStatusHistoryResponse response =
                            new EndpointStatusHistoryResponse();

                    response.setId(history.getId());
                    response.setCheckedAt(history.getCheckedAt());
                    response.setStatus(history.getStatus());
                    response.setStatusCode(history.getStatusCode());
                    response.setError(history.getError());
                    response.setResponseTimeMs(history.getResponseTimeMs());

                    return response;
                })
                .toList();
    }

    public EndpointStatsResponse getEndpointStats(Long endpointId) {

        long checks = historyRepository.countByEndpointId(endpointId);

        long failures =
                historyRepository.countByEndpointIdAndStatus(endpointId, "DOWN");

        long successfulChecks = checks - failures;

        double uptime = checks > 0
                ? ((double) successfulChecks / checks) * 100
                : 0.0;

        double downtime = checks > 0
                ? ((double) failures / checks) * 100
                : 0.0;

        Double avgResponseTime =
                historyRepository.findAverageResponseTime(endpointId);

        EndpointStatsResponse response = new EndpointStatsResponse();

        response.setEndpointId(endpointId);
        response.setUptime(Math.round(uptime * 10.0) / 10.0);
        response.setDowntime(Math.round(downtime * 10.0) / 10.0);
        response.setChecks(checks);
        response.setAvgResponseTime(
                avgResponseTime != null
                        ? Math.round(avgResponseTime * 10.0) / 10.0
                        : 0.0
        );
        response.setFailures(failures);

        return response;
    }
}