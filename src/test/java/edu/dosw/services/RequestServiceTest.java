package edu.dosw.services;

import edu.dosw.dto.RequestDTO;
import edu.dosw.dto.RequestResponse;
import edu.dosw.dto.RequestStats;
import edu.dosw.model.Group;
import edu.dosw.model.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.dosw.repositories.CourseRepository;
import edu.dosw.repositories.RequestRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    private RequestRepository requestRepository;
    private CourseRepository courseRepository;
    private RequestService requestService;

    @BeforeEach
    void setUp() {
        requestRepository = mock(RequestRepository.class);
        courseRepository = mock(CourseRepository.class);
        requestService = new RequestService(requestRepository, courseRepository);
    }

    @Test
    void fetchRequests_shouldThrowWhenRoleIsUnsupported() {
        // Arrange
        String unsupportedRole = "UNKNOWN";
        String id = "id1";

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> requestService.fetchRequests(unsupportedRole, id));
    }

    @Test
    void createRequest_shouldSaveAndReturnResponse() {
        // Arrange
        RequestDTO dto = new RequestDTO(
                UUID.randomUUID().toString(),
                "student1",
                "TYPE",
                false,
                "PENDING",
                "desc",
                "orig",
                "dest",
                null,
                null
        );

        when(courseRepository.findByCode(anyString()))
                .thenReturn(new Group("G1", "Prof", 10, 5));
        when(requestRepository.save(any(Request.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        RequestResponse result = requestService.createRequest(dto);

        // Assert
        assertEquals("student1", result.studentId());
        assertEquals("TYPE", result.type());
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void createRequest_shouldThrowWhenCourseNotFound() {
        // Arrange
        RequestDTO dto = new RequestDTO(
                UUID.randomUUID().toString(),
                "studentX",
                "TYPE",
                false,
                "PENDING",
                "desc",
                "orig",
                "dest",
                null,
                null
        );

        when(courseRepository.findByCode(anyString())).thenReturn(null);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> requestService.createRequest(dto));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void updateRequestStatus_shouldUpdateStatusWhenRequestExists() {
        // Arrange
        Request existing = new Request();
        existing.setId("1");
        existing.setStatus("PENDING");

        when(requestRepository.findById("1")).thenReturn(Optional.of(existing));
        when(requestRepository.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Request updated = requestService.updateRequestStatus("1", "APPROVED");

        // Assert
        assertEquals("APPROVED", updated.getStatus());
        verify(requestRepository).save(existing);
    }

    @Test
    void updateRequestStatus_shouldThrowWhenRequestNotFound() {
        // Arrange
        when(requestRepository.findById("404")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> requestService.updateRequestStatus("404", "APPROVED"));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestStats_shouldReturnAggregatedCounts() {
        // Arrange
        when(requestRepository.count()).thenReturn(5L);
        when(requestRepository.countByStatus("PENDING")).thenReturn(2L);
        when(requestRepository.countByStatus("APPROVED")).thenReturn(1L);
        when(requestRepository.countByStatus("REJECTED")).thenReturn(2L);

        // Act
        RequestStats stats = requestService.getRequestStats();

        // Assert
        assertEquals(5, stats.total());
        assertEquals(2, stats.pending());
        assertEquals(1, stats.approved());
        assertEquals(2, stats.rejected());
    }
}
