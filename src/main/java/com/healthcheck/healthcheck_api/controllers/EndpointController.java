package com.healthcheck.healthcheck_api.controllers;

import com.healthcheck.healthcheck_api.dto.CreateEndpointRequest;
import com.healthcheck.healthcheck_api.dto.UpdateEndpointRequest;
import com.healthcheck.healthcheck_api.models.Endpoint;
import com.healthcheck.healthcheck_api.models.EndpointStatusHistory;
import com.healthcheck.healthcheck_api.services.EndpointService;
import com.healthcheck.healthcheck_api.dto.*;
import com.healthcheck.healthcheck_api.services.EndpointStatusHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/api/endpoints")
@Validated
public class EndpointController {

    private final EndpointService endpointService;
    private final EndpointStatusHistoryService endpointStatusHistoryService;

    public EndpointController(EndpointService endpointService, EndpointStatusHistoryService endpointStatusHistoryService) {
        this.endpointService = endpointService;
        this.endpointStatusHistoryService = endpointStatusHistoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endpoint createEndpoint(
            @Valid @RequestBody CreateEndpointRequest request) {

        return endpointService.createEndpoint(request);
    }

    @GetMapping
    public List<Endpoint> getAllEndpoints() {
        return endpointService.getAllEndpoints();
    }

    @GetMapping("/{id}")
    public Endpoint getEndpointById(@PathVariable @Positive(message = "ID must be greater than 0") Long id) {
        return endpointService.getEndpointById(id);
    }

    @PutMapping("/{id}")
    public Endpoint updateEndpoint(
            @PathVariable @Positive(message = "ID must be greater than 0") Long id,
            @Valid @RequestBody UpdateEndpointRequest request) {

        return endpointService.updateEndpoint(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEndpoint(@PathVariable @Positive(message = "ID must be greater than 0") Long id) {
        endpointService.deleteEndpoint(id);
    }

    @GetMapping("/search")
    public List<Endpoint> searchEndpoints(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String url) {

        if (name != null) {
            return endpointService.searchByName(name);
        }

        if (url != null) {
            return endpointService.searchByUrl(url);
        }

        return endpointService.getAllEndpoints();
    }

    @GetMapping("/{id}/history")
    public List<EndpointStatusHistoryResponse> getEndpointHistory(
            @PathVariable Long id) {

        return endpointStatusHistoryService.getHistory(id);
    }
}