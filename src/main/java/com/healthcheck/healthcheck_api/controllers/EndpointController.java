package com.healthcheck.healthcheck_api.controllers;

import com.healthcheck.healthcheck_api.dto.CreateEndpointRequest;
import com.healthcheck.healthcheck_api.dto.EndpointStatsResponse;
import com.healthcheck.healthcheck_api.dto.EndpointStatusHistoryResponse;
import com.healthcheck.healthcheck_api.dto.UpdateEndpointRequest;
import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.services.EndpointService;
import com.healthcheck.healthcheck_api.services.EndpointStatusHistoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/endpoints")
@Validated
public class EndpointController {

    private final EndpointService endpointService;
    private final EndpointStatusHistoryService endpointStatusHistoryService;

    public EndpointController(
            EndpointService endpointService,
            EndpointStatusHistoryService endpointStatusHistoryService) {

        this.endpointService = endpointService;
        this.endpointStatusHistoryService = endpointStatusHistoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endpoint createEndpoint(
            @Valid @RequestBody CreateEndpointRequest request,
            Authentication authentication) {

        return endpointService.createEndpoint(
                request,
                authentication
        );
    }

    @GetMapping
    public List<Endpoint> getAllEndpoints(
            Authentication authentication) {

        return endpointService.getAllEndpoints(
                authentication
        );
    }

    @GetMapping("/{id}")
    public Endpoint getEndpointById(
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id,
            Authentication authentication) {

        return endpointService.getEndpointById(
                id,
                authentication
        );
    }

    @PutMapping("/{id}")
    public Endpoint updateEndpoint(
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id,
            @Valid @RequestBody UpdateEndpointRequest request,
            Authentication authentication) {

        return endpointService.updateEndpoint(
                id,
                request,
                authentication
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEndpoint(
            @PathVariable
            @Positive(message = "ID must be greater than 0")
            Long id,
            Authentication authentication) {

        endpointService.deleteEndpoint(
                id,
                authentication
        );
    }

    @GetMapping("/search")
    public List<Endpoint> searchEndpoints(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String url,
            Authentication authentication) {

        if (name != null) {
            return endpointService.searchByName(
                    name,
                    authentication
            );
        }

        if (url != null) {
            return endpointService.searchByUrl(
                    url,
                    authentication
            );
        }

        return endpointService.getAllEndpoints(
                authentication
        );
    }

    @GetMapping("/{id}/history")
    public List<EndpointStatusHistoryResponse> getEndpointHistory(
            @PathVariable Long id,
            Authentication authentication) {

        return endpointStatusHistoryService.getHistory(
                id,
                authentication
        );
    }

    @GetMapping("/{endpointId}/stats")
    public EndpointStatsResponse getEndpointStats(
            @PathVariable Long endpointId,
            Authentication authentication) {

        return endpointStatusHistoryService.getEndpointStats(
                endpointId,
                authentication
        );
    }
}