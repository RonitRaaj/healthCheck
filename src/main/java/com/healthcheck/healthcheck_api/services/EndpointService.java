package com.healthcheck.healthcheck_api.services;

import com.healthcheck.healthcheck_api.dto.CreateEndpointRequest;
import com.healthcheck.healthcheck_api.dto.UpdateEndpointRequest;
import com.healthcheck.healthcheck_api.exceptions.EndpointNotFoundException;
import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.models.User;
import com.healthcheck.healthcheck_api.repositories.EndpointRepository;
import com.healthcheck.healthcheck_api.repositories.EndpointStatusHistoryRepository;
import com.healthcheck.healthcheck_api.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EndpointService {

    private final EndpointRepository endpointRepository;
    private final UserRepository userRepository;
    private final EndpointStatusHistoryRepository historyRepository;
    private final HealthCheckService healthCheckService;

    public EndpointService(
            EndpointRepository endpointRepository,
            UserRepository userRepository,
            EndpointStatusHistoryRepository historyRepository,
            HealthCheckService healthCheckService) {

        this.endpointRepository = endpointRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.healthCheckService = healthCheckService;
    }

    public Endpoint createEndpoint(
            CreateEndpointRequest request,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

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
        endpoint.setUser(user);

        LocalDateTime now = LocalDateTime.now();

        endpoint.setNextCheckAt(
                now.plusMinutes(
                        endpoint.getCheckIntervalMinutes()
                )
        );

        endpoint = endpointRepository.save(endpoint);

        healthCheckService.checkEndpoint(endpoint);

        return endpoint;
    }

    public List<Endpoint> getAllEndpoints(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return endpointRepository.findByUserId(user.getId());
    }

    public Endpoint getEndpointById(
            Long id,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return endpointRepository
                .findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EndpointNotFoundException(id));
    }

    public Endpoint updateEndpoint(
            Long id,
            UpdateEndpointRequest request,
            Authentication authentication) {

        Endpoint existingEndpoint =
                getEndpointById(id, authentication);

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

    @Transactional
    public void deleteEndpoint(
            Long id,
            Authentication authentication) {

        Endpoint endpoint =
                getEndpointById(id, authentication);

        historyRepository.deleteAll(
                historyRepository
                        .findByEndpointIdOrderByCheckedAtDesc(id)
        );

        endpointRepository.delete(endpoint);
    }

    public List<Endpoint> searchByName(
            String name,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return endpointRepository
                .findByUserId(user.getId())
                .stream()
                .filter(endpoint ->
                        endpoint.getName()
                                .toLowerCase()
                                .contains(name.toLowerCase()))
                .toList();
    }

    public List<Endpoint> searchByUrl(
            String url,
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return endpointRepository
                .findByUserId(user.getId())
                .stream()
                .filter(endpoint ->
                        endpoint.getUrl()
                                .toLowerCase()
                                .contains(url.toLowerCase()))
                .toList();
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }
}