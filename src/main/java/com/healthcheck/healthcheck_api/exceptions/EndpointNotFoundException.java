package com.healthcheck.healthcheck_api.exceptions;

public class EndpointNotFoundException extends RuntimeException {

    public EndpointNotFoundException(Long id) {
        super("Endpoint with id " + id + " not found");
    }
}