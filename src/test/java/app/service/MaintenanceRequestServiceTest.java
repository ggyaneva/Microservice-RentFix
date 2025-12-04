package app.service;

import app.exception.MaintenanceNotFoundException;
import app.model.MaintenanceRequest;
import app.model.enums.MaintenanceStatus;
import app.repository.MaintenanceRepository;
import app.web.dto.MaintenanceCreateRequest;
import app.web.dto.MaintenanceResponse;
import app.web.dto.MaintenanceUpdateStatusRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceRequestServiceTest {

    @Mock
    private MaintenanceRepository repository;

    @InjectMocks
    private MaintenanceRequestService service;

    @Test
    void create_ShouldMapFieldsAndSave() {
        UUID propertyId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        MaintenanceCreateRequest request = new MaintenanceCreateRequest();
        request.setPropertyId(propertyId);
        request.setTenantId(tenantId);
        request.setOwnerId(ownerId);
        request.setDescription("Leaking sink");

        MaintenanceResponse response = service.create(request);

        ArgumentCaptor<MaintenanceRequest> captor = ArgumentCaptor.forClass(MaintenanceRequest.class);
        verify(repository).save(captor.capture());
        MaintenanceRequest saved = captor.getValue();

        assertThat(saved.getPropertyId()).isEqualTo(propertyId);
        assertThat(saved.getTenantId()).isEqualTo(tenantId);
        assertThat(saved.getOwnerId()).isEqualTo(ownerId);
        assertThat(saved.getDescription()).isEqualTo("Leaking sink");
        assertThat(saved.getStatus()).isEqualTo(MaintenanceStatus.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();

        assertThat(response.getPropertyId()).isEqualTo(propertyId);
        assertThat(response.getTenantId()).isEqualTo(tenantId);
        assertThat(response.getOwnerId()).isEqualTo(ownerId);
        assertThat(response.getDescription()).isEqualTo("Leaking sink");
        assertThat(response.getStatus()).isEqualTo(MaintenanceStatus.PENDING);
        assertThat(response.getCreatedAt()).isNotNull();
    }

    @Test
    void updateStatus_ShouldThrow_WhenIdNotFound() {
        UUID id = UUID.randomUUID();
        MaintenanceUpdateStatusRequest request = new MaintenanceUpdateStatusRequest();
        request.setStatus(MaintenanceStatus.IN_PROGRESS);

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MaintenanceNotFoundException.class,
                () -> service.updateStatus(id, request));

        verify(repository, never()).save(any());
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_ShouldThrow_WhenNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        assertThrows(MaintenanceNotFoundException.class,
                () -> service.delete(id));

        verify(repository, never()).deleteById(any());
    }
}
