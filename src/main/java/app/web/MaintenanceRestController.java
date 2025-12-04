package app.web;


import app.web.dto.MaintenanceCreateRequest;
import app.web.dto.MaintenanceResponse;
import app.web.dto.MaintenanceUpdateStatusRequest;
import app.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceRestController {

    private final MaintenanceRequestService maintenanceRequestService;

    @Autowired
    public MaintenanceRestController(MaintenanceRequestService maintenanceRequestService) {
        this.maintenanceRequestService = maintenanceRequestService;
    }

    @PostMapping
    public MaintenanceResponse create(@Valid @RequestBody MaintenanceCreateRequest request) {
        return maintenanceRequestService.create(request);
    }

    @PutMapping("/{id}/status")
    public MaintenanceResponse updateStatus(@PathVariable UUID id,
                                            @Valid @RequestBody MaintenanceUpdateStatusRequest request) {
        return maintenanceRequestService.updateStatus(id, request);
    }

    @GetMapping("/property/{propertyId}")
    public List<MaintenanceResponse> getByProperty(@PathVariable UUID propertyId) {
        return maintenanceRequestService.getByProperty(propertyId);
    }

    @GetMapping("/all")
    public List<MaintenanceResponse> getAll() {
        return maintenanceRequestService.getAll();
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        maintenanceRequestService.delete(id);
    }

}
