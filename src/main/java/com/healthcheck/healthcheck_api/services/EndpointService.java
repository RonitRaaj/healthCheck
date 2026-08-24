package com.healthcheck.healthcheck_api.services;

import com.healthcheck.healthcheck_api.dto.CreateEndpointRequest;
import com.healthcheck.healthcheck_api.dto.UpdateEndpointRequest;
import com.healthcheck.healthcheck_api.exceptions.EndpointNotFoundException;
import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.repositories.EndpointRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndpointService {

    private final EndpointRepository endpointRepository;
    private final HealthCheckService healthCheckService;

    public EndpointService(EndpointRepository endpointRepository ,  HealthCheckService healthCheckService) {
        this.endpointRepository = endpointRepository;
        this.healthCheckService = healthCheckService;
    }

    public Endpoint createEndpoint(CreateEndpointRequest request) {

        Endpoint endpoint = new Endpoint();

        endpoint.setName(request.getName());
        endpoint.setUrl(request.getUrl());
        endpoint.setCheckIntervalMinutes(
                request.getCheckIntervalMinutes()
        );

        endpoint.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );

        endpoint.setStatus("UNKNOWN");

        LocalDateTime now = LocalDateTime.now();

        endpoint.setNextCheckAt(
                now.plusMinutes(endpoint.getCheckIntervalMinutes())
        );

        endpoint = endpointRepository.save(endpoint);

        healthCheckService.checkEndpoint(endpoint);

        return endpoint;
    }

    public List<Endpoint> getAllEndpoints() {
        return endpointRepository.findAll();
    }

    public Endpoint getEndpointById(Long id) {
        return endpointRepository.findById(id)
                .orElseThrow(() -> new EndpointNotFoundException(id));
    }

    public Endpoint updateEndpoint(
            Long id,
            UpdateEndpointRequest request) {

        Endpoint existingEndpoint = getEndpointById(id);

        if (request.getName() != null) {
            existingEndpoint.setName(request.getName());
        }

        if (request.getUrl() != null) {
            existingEndpoint.setUrl(request.getUrl());
        }

        if (request.getCheckIntervalMinutes() != null) {
            existingEndpoint.setCheckIntervalMinutes(
                    request.getCheckIntervalMinutes()
            );
        }

        if (request.getActive() != null) {
            existingEndpoint.setActive(request.getActive());
        }

        return endpointRepository.save(existingEndpoint);
    }

    public void deleteEndpoint(Long id) {

        Endpoint endpoint = getEndpointById(id);

        endpointRepository.delete(endpoint);
    }

    public List<Endpoint> searchByName(String name) {
        return endpointRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Endpoint> searchByUrl(String url) {
        return endpointRepository.findByUrlContainingIgnoreCase(url);
    }
}