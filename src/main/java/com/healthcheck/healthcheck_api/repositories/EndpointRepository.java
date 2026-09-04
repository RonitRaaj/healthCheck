package com.healthcheck.healthcheck_api.repositories;

import com.healthcheck.healthcheck_api.models.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    List<Endpoint> findByNameContainingIgnoreCase(String name);

    List<Endpoint> findByUrlContainingIgnoreCase(String url);

    List<Endpoint> findByActiveTrueAndNextCheckAtLessThanEqual(
            LocalDateTime currentTime
    );

    List<Endpoint> findByUserId(Long userId);

    Optional<Endpoint> findByIdAndUserId(Long endpointId, Long userId);
}