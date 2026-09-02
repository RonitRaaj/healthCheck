package com.healthcheck.healthcheck_api.repositories;

import com.healthcheck.healthcheck_api.models.EndpointStatusHistory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointStatusHistoryRepository
        extends JpaRepository<EndpointStatusHistory, Long> {

    List<EndpointStatusHistory> findByEndpointIdOrderByCheckedAtDesc(Long endpointId);

    @Transactional
    long deleteByCheckedAtBefore(LocalDateTime cutoff);

    long countByEndpointId(Long endpointId);

    long countByEndpointIdAndStatus(Long endpointId, String status);

    @Query("""
    SELECT COUNT(h)
    FROM EndpointStatusHistory h
    WHERE h.checkedAt < :cutoff
""")
    long countOlderThan(@Param("cutoff") LocalDateTime cutoff);

    @Query("""
        SELECT AVG(h.responseTimeMs)
        FROM EndpointStatusHistory h
        WHERE h.endpoint.id = :endpointId
        AND h.responseTimeMs IS NOT NULL
    """)
    Double findAverageResponseTime(@Param("endpointId") Long endpointId);


    @Query("""
    SELECT MIN(h.checkedAt)
    FROM EndpointStatusHistory h
    WHERE h.endpoint.id = :endpointId
""")
    LocalDateTime findOldestCheckedAt(@Param("endpointId") Long endpointId);
}