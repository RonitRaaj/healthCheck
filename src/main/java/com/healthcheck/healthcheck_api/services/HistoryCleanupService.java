package com.healthcheck.healthcheck_api.services;

import com.healthcheck.healthcheck_api.repositories.EndpointStatusHistoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoryCleanupService {

    private final EndpointStatusHistoryRepository historyRepository;

    public HistoryCleanupService(
            EndpointStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void deleteOldHistory() {

        LocalDateTime cutoff =
                LocalDateTime.now().minusHours(72);

        long deleted =
                historyRepository.deleteByCheckedAtBefore(cutoff);

        System.out.println(
                "Deleted " + deleted +
                        " history records older than: " + cutoff
        );
    }
}