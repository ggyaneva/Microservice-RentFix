package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MaintenanceCreateRequest {

    @NotNull
    private UUID propertyId;

    @NotNull
    private UUID tenantId;

    private UUID ownerId;

    @NotBlank
    private String description;

}
