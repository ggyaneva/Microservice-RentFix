package app.scheduler;


import app.model.enums.MaintenanceStatus;
import app.repository.MaintenanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MaintenanceSchedulers {

    private final MaintenanceRepository repository;

    public MaintenanceSchedulers(MaintenanceRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void autoCompleteOld() {
        var older = repository.findAllByStatusAndCreatedAtBefore(
                MaintenanceStatus.IN_PROGRESS,
                LocalDateTime.now().minusDays(30)
        );

        older.forEach(m -> m.setStatus(MaintenanceStatus.COMPLETED));
        if (!older.isEmpty()) {
            repository.saveAll(older);
            log.info("Auto-completed {} old requests", older.size());
        }
    }

    @Scheduled(fixedDelay = 600000)
    public void logCount() {
        long count = repository.count();
        log.info("Total maintenance requests: {}", count);
    }
}
