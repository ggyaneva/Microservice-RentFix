package app.web;

import app.exception.MaintenanceNotFoundException;
import app.model.enums.MaintenanceStatus;
import app.service.MaintenanceRequestService;
import app.web.dto.MaintenanceCreateRequest;
import app.web.dto.MaintenanceResponse;
import app.web.dto.MaintenanceUpdateStatusRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({MaintenanceRestController.class, GlobalExceptionHandler.class})
class MaintenanceRestControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaintenanceRequestService maintenanceRequestService;

    @Test
    void create_ShouldReturnCreatedMaintenance() throws Exception {
        UUID id = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        MaintenanceCreateRequest request = new MaintenanceCreateRequest();
        request.setPropertyId(propertyId);
        request.setTenantId(tenantId);
        request.setOwnerId(ownerId);
        request.setDescription("Some issue");

        MaintenanceResponse response = new MaintenanceResponse();
        response.setId(id);
        response.setPropertyId(propertyId);
        response.setTenantId(tenantId);
        response.setOwnerId(ownerId);
        response.setDescription("Some issue");
        response.setStatus(MaintenanceStatus.PENDING);
        response.setCreatedAt(LocalDateTime.now());

        given(maintenanceRequestService.create(any(MaintenanceCreateRequest.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.propertyId").value(propertyId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // description е @NotBlank → пращаме празен низ
        String body = "{"
                + "\"propertyId\":\"" + UUID.randomUUID() + "\","
                + "\"tenantId\":\"" + UUID.randomUUID() + "\","
                + "\"description\":\"\""
                + "}";

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.description", notNullValue()));
    }

    @Test
    void updateStatus_ShouldReturnNotFound_WhenMaintenanceDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        MaintenanceUpdateStatusRequest request = new MaintenanceUpdateStatusRequest();
        request.setStatus(MaintenanceStatus.IN_PROGRESS);

        given(maintenanceRequestService.updateStatus(eq(id), any(MaintenanceUpdateStatusRequest.class)))
                .willThrow(new MaintenanceNotFoundException(id));

        mockMvc.perform(put("/api/maintenance/{id}/status", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error", containsString(id.toString())));
    }

    @Test
    void getByProperty_ShouldReturnListOfResponses() throws Exception {
        UUID propertyId = UUID.randomUUID();

        MaintenanceResponse r1 = new MaintenanceResponse();
        r1.setId(UUID.randomUUID());
        r1.setPropertyId(propertyId);
        r1.setTenantId(UUID.randomUUID());
        r1.setDescription("Issue 1");
        r1.setStatus(MaintenanceStatus.PENDING);
        r1.setCreatedAt(LocalDateTime.now());

        MaintenanceResponse r2 = new MaintenanceResponse();
        r2.setId(UUID.randomUUID());
        r2.setPropertyId(propertyId);
        r2.setTenantId(UUID.randomUUID());
        r2.setDescription("Issue 2");
        r2.setStatus(MaintenanceStatus.IN_PROGRESS);
        r2.setCreatedAt(LocalDateTime.now());

        given(maintenanceRequestService.getByProperty(propertyId))
                .willReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/maintenance/property/{propertyId}", propertyId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("Issue 1"))
                .andExpect(jsonPath("$[1].description").value("Issue 2"));
    }

    @Test
    void delete_ShouldReturnOk_WhenExists() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/maintenance/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void delete_ShouldReturnNotFound_WhenDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new MaintenanceNotFoundException(id))
                .when(maintenanceRequestService).delete(id);

        mockMvc.perform(delete("/api/maintenance/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
