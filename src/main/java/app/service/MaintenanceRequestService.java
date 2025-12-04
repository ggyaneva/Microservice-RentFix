package app.service;

import app.model.enums.MaintenanceStatus;
import app.web.dto.*;
import app.exception.MaintenanceNotFoundException;
import app.model.MaintenanceRequest;
import app.repository.MaintenanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MaintenanceRequestService {

    private final MaintenanceRepository repository;

    @Autowired
    public MaintenanceRequestService(MaintenanceRepository repository) {
        this.repository = repository;
    }

    @CacheEvict(value = "maintenanceByProperty", key = "#request.propertyId")
    public MaintenanceResponse create(MaintenanceCreateRequest request) {

        log.info("Creating maintenance for property {}", request.getPropertyId());

        MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
        maintenanceRequest.setPropertyId(request.getPropertyId());
        maintenanceRequest.setTenantId(request.getTenantId());
        maintenanceRequest.setOwnerId(request.getOwnerId());
        maintenanceRequest.setDescription(request.getDescription());
        maintenanceRequest.setStatus(MaintenanceStatus.PENDING);
        maintenanceRequest.setCreatedAt(LocalDateTime.now());

        repository.save(maintenanceRequest);
        return map(maintenanceRequest);
    }

    @CacheEvict(value = "maintenanceByProperty", allEntries = true)
    public MaintenanceResponse updateStatus(UUID id, MaintenanceUpdateStatusRequest request) {

        log.info("Updating status of maintenance {}", id);

        MaintenanceRequest entity = repository.findById(id)
                .orElseThrow(() -> new MaintenanceNotFoundException(id));

        entity.setStatus(request.getStatus());
        repository.save(entity);

        return map(entity);
    }

    @Cacheable(value = "maintenanceByProperty", key = "#propertyId")
    public List<MaintenanceResponse> getByProperty(UUID propertyId) {
        return repository.findAllByPropertyIdOrderByCreatedAtDesc(propertyId).stream().map(this::map).toList();
    }

    public List<MaintenanceResponse> getAll() {
        return repository.findAll().stream().map(this::map).toList();
    }

    private MaintenanceResponse map(MaintenanceRequest request) {
        MaintenanceResponse response = new MaintenanceResponse();
        response.setId(request.getId());
        response.setPropertyId(request.getPropertyId());
        response.setTenantId(request.getTenantId());
        response.setOwnerId(request.getOwnerId());
        response.setDescription(request.getDescription());
        response.setStatus(request.getStatus());
        response.setCreatedAt(request.getCreatedAt());
        return response;
    }
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new MaintenanceNotFoundException(id);
        }
        repository.deleteById(id);
    }

}

