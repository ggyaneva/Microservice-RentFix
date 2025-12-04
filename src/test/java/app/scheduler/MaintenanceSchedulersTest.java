package app.scheduler;

import app.model.MaintenanceRequest;
import app.model.enums.MaintenanceStatus;
import app.repository.MaintenanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceSchedulersTest {

    @Mock
    private MaintenanceRepository repository;

    @InjectMocks
    private MaintenanceSchedulers schedulers;

    @Test
    void autoCompleteOld_ShouldCompleteAndSaveOldInProgressRequests() {
        MaintenanceRequest old = new MaintenanceRequest();
        old.setStatus(MaintenanceStatus.IN_PROGRESS);
        old.setCreatedAt(LocalDateTime.now().minusDays(40));

        when(repository.findAllByStatusAndCreatedAtBefore(
                eq(MaintenanceStatus.IN_PROGRESS),
                any(LocalDateTime.class)
        )).thenReturn(List.of(old));

        schedulers.autoCompleteOld();

        assertThat(old.getStatus()).isEqualTo(MaintenanceStatus.COMPLETED);
        verify(repository).saveAll(List.of(old));
    }

    @Test
    void logCount_ShouldCallRepositoryCount() {
        when(repository.count()).thenReturn(5L);

        schedulers.logCount();

        verify(repository).count();
    }
}
