package app.repository;

import app.model.MaintenanceRequest;
import app.model.enums.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaintenanceRepositoryIntegrationTest {

    @Autowired
    private MaintenanceRepository repository;

    @Test
    void findAllByPropertyIdOrderByCreatedAtDesc_ShouldReturnOrderedResults() {
        UUID propertyId = UUID.randomUUID();

        MaintenanceRequest older = new MaintenanceRequest();
        older.setPropertyId(propertyId);
        older.setTenantId(UUID.randomUUID());
        older.setDescription("Old");
        older.setStatus(MaintenanceStatus.PENDING);
        older.setCreatedAt(LocalDateTime.now().minusDays(2));

        MaintenanceRequest newer = new MaintenanceRequest();
        newer.setPropertyId(propertyId);
        newer.setTenantId(UUID.randomUUID());
        newer.setDescription("New");
        newer.setStatus(MaintenanceStatus.PENDING);
        newer.setCreatedAt(LocalDateTime.now());

        repository.save(older);
        repository.save(newer);

        List<MaintenanceRequest> result =
                repository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getDescription()).isEqualTo("New");
        assertThat(result.get(1).getDescription()).isEqualTo("Old");
    }

    @Test
    void findAllByStatusAndCreatedAtBefore_ShouldReturnOnlyOlderInProgressRequests() {
        MaintenanceRequest oldInProgress = new MaintenanceRequest();
        oldInProgress.setPropertyId(UUID.randomUUID());
        oldInProgress.setTenantId(UUID.randomUUID());
        oldInProgress.setDescription("Old IN_PROGRESS");
        oldInProgress.setStatus(MaintenanceStatus.IN_PROGRESS);
        oldInProgress.setCreatedAt(LocalDateTime.now().minusDays(31));

        MaintenanceRequest newInProgress = new MaintenanceRequest();
        newInProgress.setPropertyId(UUID.randomUUID());
        newInProgress.setTenantId(UUID.randomUUID());
        newInProgress.setDescription("New IN_PROGRESS");
        newInProgress.setStatus(MaintenanceStatus.IN_PROGRESS);
        newInProgress.setCreatedAt(LocalDateTime.now().minusDays(5));

        MaintenanceRequest oldCompleted = new MaintenanceRequest();
        oldCompleted.setPropertyId(UUID.randomUUID());
        oldCompleted.setTenantId(UUID.randomUUID());
        oldCompleted.setDescription("Old COMPLETED");
        oldCompleted.setStatus(MaintenanceStatus.COMPLETED);
        oldCompleted.setCreatedAt(LocalDateTime.now().minusDays(40));

        repository.save(oldInProgress);
        repository.save(newInProgress);
        repository.save(oldCompleted);

        List<MaintenanceRequest> result =
                repository.findAllByStatusAndCreatedAtBefore(
                        MaintenanceStatus.IN_PROGRESS,
                        LocalDateTime.now().minusDays(30)
                );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Old IN_PROGRESS");
    }
}
