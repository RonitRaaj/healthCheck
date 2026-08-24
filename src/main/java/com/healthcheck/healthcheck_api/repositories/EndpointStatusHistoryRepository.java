package com.healthcheck.healthcheck_api.repositories;

import com.healthcheck.healthcheck_api.models.EndpointStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointStatusHistoryRepository
        extends JpaRepository<EndpointStatusHistory, Long> {

    List<EndpointStatusHistory> findByEndpointIdOrderByCheckedAtDesc(Long endpointId);
    void deleteByCheckedAtBefore(LocalDateTime cutoff);
}