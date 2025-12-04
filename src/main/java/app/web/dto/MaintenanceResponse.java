package app.web.dto;

import app.model.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MaintenanceResponse {

    private UUID id;
    private UUID propertyId;
    private UUID tenantId;
    private UUID ownerId;
    @NotBlank
    private String description;
    private MaintenanceStatus status;
    private LocalDateTime createdAt;
}
