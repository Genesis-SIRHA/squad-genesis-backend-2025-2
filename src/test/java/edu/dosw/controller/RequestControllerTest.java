package edu.dosw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import edu.dosw.services.RequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestService requestService;

    @Test
    void should_create_request() throws Exception {

        RequestDTO dto = new RequestDTO(
                null,
                "student123",
                "GROUP_CHANGE",
                false,
                null,
                "Cambio de grupo",
                "G1",
                "G2",
                null,
                null
        );


        RequestDTO responseDTO = new RequestDTO(
                "req1",
                "student123",
                "GROUP_CHANGE",
                false,
                "PENDING",
                "Cambio de grupo",
                "G1",
                "G2",
                null,
                null
        );


        when(requestService.createRequest(any(RequestDTO.class))).thenReturn(responseDTO.toEntity());

        // Act & Assert
        mockMvc.perform(post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("req1"))
                .andExpect(jsonPath("$.studentId").value("student123"))
                .andExpect(jsonPath("$.description").value("Cambio de grupo"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void should_fetch_requests_by_role() throws Exception {
        // Arrange
        Group originGroup = new Group("G1", "Profesor A", 30, 10);
        Group destinationGroup = new Group("G2", "Profesor B", 30, 5);

        Request request = new Request("student123", "Cambio de grupo", "GROUP_CHANGE",
                originGroup.getGroupCode(), destinationGroup.getGroupCode());
        request.setStatus("PENDING");

        when(requestService.fetchRequests("ADMIN", "student123"))
                .thenReturn(List.of(request));

        // Act & Assert
        mockMvc.perform(get("/api/requests/{userId}/role", "student123")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value("student123"))
                .andExpect(jsonPath("$[0].description").value("Cambio de grupo"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }



    @Test
    void should_update_request_status() throws Exception {
        Request updatedRequest = new Request();
        updatedRequest.setId("req1");
        updatedRequest.setStatus("APPROVED");

        when(requestService.updateRequestStatus("req1", "APPROVED")).thenReturn(updatedRequest);

        mockMvc.perform(put("/api/requests/req1/status")
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void should_get_request_stats() throws Exception {
        // Arrange
        RequestStats stats = new RequestStats(10, 5, 3, 2);
        when(requestService.getRequestStats()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/requests/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.pending").value(5))
                .andExpect(jsonPath("$.approved").value(3))
                .andExpect(jsonPath("$.rejected").value(2));
    }


    @Test
    void should_cancel_request() throws Exception {
        mockMvc.perform(delete("/api/requests/req1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void respondToRequest_ShouldReturnOk_WhenRequestExists() throws Exception {
        Request request = new Request();
        request.setId("123");
        request.setStatus("APPROVED");

        when(requestService.respondToRequest(any(), any())).thenReturn(request);

        Request details = new Request();
        details.setAnswer("APPROVED");
        details.setGestedBy("professor1");

        mockMvc.perform(post("/api/requests/123/respond")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void respondToRequest_ShouldReturnNotFound_WhenRequestDoesNotExist() throws Exception {
        when(requestService.respondToRequest(any(), any())).thenReturn(null);

        Request details = new Request();
        details.setAnswer("REJECTED");

        mockMvc.perform(post("/requests/999/respond")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(details)))
                .andExpect(status().isNotFound());
    }
}
