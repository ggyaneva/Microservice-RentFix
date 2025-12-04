package app.web.dto;

import app.model.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaintenanceUpdateStatusRequest {

    @NotNull
    private MaintenanceStatus status;
}
