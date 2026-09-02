package com.healthcheck.healthcheck_api.scheduler;

import com.healthcheck.healthcheck_api.services.HistoryCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HistoryCleanupScheduler {

    private final HistoryCleanupService historyCleanupService;

    public HistoryCleanupScheduler(
            HistoryCleanupService historyCleanupService) {
        this.historyCleanupService = historyCleanupService;
    }

    @Scheduled(fixedRate = 360000)
    public void cleanupOldHistory() {

        System.out.println("Running history cleanup...");

        historyCleanupService.deleteOldHistory();
    }
}