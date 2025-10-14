package edu.dosw.controller;

import edu.dosw.model.Course;
import edu.dosw.model.Pemsum;
import edu.dosw.services.PemsumService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PemsumControllerTest {

    @Mock
    private PemsumService pemsumService;

    @InjectMocks
    private PemsumController pemsumController;

    @Test
    void getPemsum_WhenStudentExists_ShouldReturnPemsum() {
        String studentId = "STU001";
        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        Map<Course, String> courses = Map.of(course1, "Approved", course2, "In Progress");

        Pemsum expectedPemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("John Doe")
                .facultyName("Engineering")
                .facultyPlan("2024")
                .approvedCredits(45)
                .totalCredits(150)
                .courses(courses)
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(expectedPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentId, response.getBody().getStudentId());
        assertEquals("John Doe", response.getBody().getStudentName());
        assertEquals(45, response.getBody().getApprovedCredits());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_WhenStudentNotExists_ShouldReturnEmptyPemsum() {
        String studentId = "NONEXISTENT";
        Pemsum emptyPemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("")
                .facultyName("")
                .facultyPlan("")
                .approvedCredits(0)
                .totalCredits(0)
                .courses(Map.of())
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(emptyPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentId, response.getBody().getStudentId());
        assertEquals(0, response.getBody().getApprovedCredits());
        assertTrue(response.getBody().getCourses().isEmpty());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_WhenServiceThrowsException_ShouldPropagateException() {
        String studentId = "STU001";
        when(pemsumService.getPemsum(studentId)).thenThrow(new RuntimeException("Student not found"));

        assertThrows(RuntimeException.class, () -> pemsumController.getPemsum(studentId));
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_WithCompleteCredits_ShouldReturnCompletedPemsum() {
        String studentId = "STU002";
        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("CS101", "Programming", 3);
        Map<Course, String> courses = Map.of(course1, "Approved", course2, "Approved");

        Pemsum completedPemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("Jane Smith")
                .facultyName("Computer Science")
                .facultyPlan("2024")
                .approvedCredits(150)
                .totalCredits(150)
                .courses(courses)
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(completedPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150, response.getBody().getApprovedCredits());
        assertEquals(150, response.getBody().getTotalCredits());
        assertEquals(2, response.getBody().getCourses().size());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_WithMultipleCourses_ShouldReturnAllCourses() {
        String studentId = "STU003";
        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        Course course3 = new Course("CHEM101", "Chemistry", 3);
        Course course4 = new Course("BIO101", "Biology", 3);
        Map<Course, String> courses = Map.of(
                course1, "Approved",
                course2, "In Progress",
                course3, "Failed",
                course4, "Approved"
        );

        Pemsum pemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("Bob Wilson")
                .facultyName("Science")
                .facultyPlan("2024")
                .approvedCredits(75)
                .totalCredits(150)
                .courses(courses)
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(pemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().getCourses().size());
        assertTrue(response.getBody().getCourses().containsValue("Failed"));
        assertTrue(response.getBody().getCourses().containsValue("In Progress"));
        verify(pemsumService, times(1)).getPemsum(studentId);
    }



    @Test
    void getPemsum_WithZeroCredits_ShouldReturnValidPemsum() {
        String studentId = "STU005";
        Pemsum zeroCreditsPemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("New Student")
                .facultyName("Undecided")
                .facultyPlan("2024")
                .approvedCredits(0)
                .totalCredits(150)
                .courses(Map.of())
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(zeroCreditsPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getApprovedCredits());
        assertEquals(150, response.getBody().getTotalCredits());
        assertTrue(response.getBody().getCourses().isEmpty());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_VerifyResponseStructure() {
        String studentId = "STU006";
        Course course = new Course("INTRO101", "Introduction", 2);
        Map<Course, String> courses = Map.of(course, "Approved");

        Pemsum pemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName("Test Student")
                .facultyName("Test Faculty")
                .facultyPlan("2024")
                .approvedCredits(10)
                .totalCredits(150)
                .courses(courses)
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(pemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Pemsum.class, response.getBody().getClass());
        assertNotNull(response.getBody().getStudentId());
        assertNotNull(response.getBody().getStudentName());
        assertNotNull(response.getBody().getFacultyName());
        assertNotNull(response.getBody().getCourses());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }

    @Test
    void getPemsum_WithDifferentStudentIds_ShouldReturnDifferentPemsums() {
        String studentId1 = "STU007";
        String studentId2 = "STU008";

        Pemsum pemsum1 = new Pemsum.Builder()
                .studentId(studentId1)
                .studentName("Student One")
                .facultyName("Engineering")
                .facultyPlan("2024")
                .approvedCredits(50)
                .totalCredits(150)
                .courses(Map.of())
                .build();

        Pemsum pemsum2 = new Pemsum.Builder()
                .studentId(studentId2)
                .studentName("Student Two")
                .facultyName("Science")
                .facultyPlan("2024")
                .approvedCredits(75)
                .totalCredits(150)
                .courses(Map.of())
                .build();

        when(pemsumService.getPemsum(studentId1)).thenReturn(pemsum1);
        when(pemsumService.getPemsum(studentId2)).thenReturn(pemsum2);

        ResponseEntity<Pemsum> response1 = pemsumController.getPemsum(studentId1);
        ResponseEntity<Pemsum> response2 = pemsumController.getPemsum(studentId2);

        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Student One", response1.getBody().getStudentName());
        assertEquals("Student Two", response2.getBody().getStudentName());
        assertEquals(50, response1.getBody().getApprovedCredits());
        assertEquals(75, response2.getBody().getApprovedCredits());
        verify(pemsumService, times(1)).getPemsum(studentId1);
        verify(pemsumService, times(1)).getPemsum(studentId2);
    }

    @Test
    void getPemsum_ShouldHandleNullValuesGracefully() {
        String studentId = "STU009";
        Pemsum nullPemsum = new Pemsum.Builder()
                .studentId(studentId)
                .studentName(null)
                .facultyName(null)
                .facultyPlan(null)
                .approvedCredits(0)
                .totalCredits(0)
                .courses(null)
                .build();

        when(pemsumService.getPemsum(studentId)).thenReturn(nullPemsum);

        ResponseEntity<Pemsum> response = pemsumController.getPemsum(studentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(studentId, response.getBody().getStudentId());
        assertNull(response.getBody().getStudentName());
        assertNull(response.getBody().getFacultyName());
        assertNull(response.getBody().getCourses());
        verify(pemsumService, times(1)).getPemsum(studentId);
    }
}