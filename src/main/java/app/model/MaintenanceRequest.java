package app.model;

import app.model.enums.MaintenanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    private UUID propertyId;

    @NotNull
    private UUID tenantId;

    private UUID ownerId;

    @NotBlank
    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    private LocalDateTime createdAt;


}
