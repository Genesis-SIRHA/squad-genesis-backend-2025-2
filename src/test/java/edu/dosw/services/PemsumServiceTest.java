package edu.dosw.services;

import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.*;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.UserServices.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PemsumServiceTest {

    @Mock
    private FacultyService facultyService;

    @Mock
    private StudentService studentService;

    @Mock
    private HistorialService historialService;

    @Mock
    private GroupService groupService;

    private PemsumService pemsumService;

    @BeforeEach
    void setUp() {
        pemsumService = new PemsumService(facultyService, studentService, historialService, groupService);
    }

    @Test
    void getPemsum_ShouldCalculateApprovedCreditsCorrectly_WithFINISHEDStatus() {
        String studentId = "STU001";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");
        student.setFullName("John Doe");

        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        List<Course> courses = Arrays.asList(course1, course2);

        Historial historial1 = new Historial();
        historial1.setGroupCode("MATH101");
        historial1.setStatus(HistorialStatus.FINISHED);

        Historial historial2 = new Historial();
        historial2.setGroupCode("PHYS101");
        historial2.setStatus(HistorialStatus.ON_GOING);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
        when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(Arrays.asList(historial1, historial2));

        Pemsum result = pemsumService.getPemsum(studentId);

        assertNotNull(result);
        assertEquals(4, result.getApprovedCredits());
        assertEquals(7, result.getTotalCredits());

        Map<Course, HistorialStatus> coursesMap = result.getCourses();
        assertEquals(HistorialStatus.FINISHED, coursesMap.get(course1));
        assertEquals(HistorialStatus.ON_GOING, coursesMap.get(course2));
    }

    @Test
    void getPemsum_ShouldNotCountNonFINISHEDStatus_AsApprovedCredits() {
        String studentId = "STU002";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Science");
        student.setPlan("2024");
        student.setFullName("Jane Smith");

        Course course1 = new Course("CHEM101", "Chemistry", 3);
        Course course2 = new Course("BIO101", "Biology", 4);
        List<Course> courses = Arrays.asList(course1, course2);

        Historial historial1 = new Historial();
        historial1.setGroupCode("CHEM101");
        historial1.setStatus(HistorialStatus.FAILED);

        Historial historial2 = new Historial();
        historial2.setGroupCode("BIO101");
        historial2.setStatus(HistorialStatus.ON_GOING);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(courses);
        when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(Arrays.asList(historial1, historial2));

        Pemsum result = pemsumService.getPemsum(studentId);

        assertNotNull(result);
        assertEquals(0, result.getApprovedCredits());
        assertEquals(7, result.getTotalCredits());

        Map<Course, HistorialStatus> coursesMap = result.getCourses();
        assertEquals(HistorialStatus.FAILED, coursesMap.get(course1));
        assertEquals(HistorialStatus.ON_GOING, coursesMap.get(course2));
    }

    @Test
    void getPemsum_WithNoHistorial_ShouldSetAllToPendingAndZeroApprovedCredits() {
        String studentId = "STU005";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");
        student.setFullName("Charlie Davis");

        List<Course> courses = Arrays.asList(
                new Course("MATH101", "Calculus I", 4),
                new Course("PHYS101", "Physics I", 3));

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(courses);
        when(historialService.getSessionsByCourses(studentId, courses)).thenReturn(List.of());

        Pemsum result = pemsumService.getPemsum(studentId);

        assertNotNull(result);
        assertEquals(0, result.getApprovedCredits());
        assertEquals(7, result.getTotalCredits());

        Map<Course, HistorialStatus> coursesMap = result.getCourses();
        assertEquals(HistorialStatus.PENDING, coursesMap.get(courses.get(0)));
        assertEquals(HistorialStatus.PENDING, coursesMap.get(courses.get(1)));
    }

    @Test
    void getPemsum_WhenFacultyCoursesNotFound_ShouldThrowException() {
        String studentId = "STU006";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Unknown");
        student.setPlan("2024");

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Unknown", "2024")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> pemsumService.getPemsum(studentId));
    }

    @Test
    void getCompletedCoursesPercentage_ShouldCalculateCorrectPercentage() {
        String studentId = "STU007";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");

        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        Course course3 = new Course("CHEM101", "Chemistry", 3);
        List<Course> facultyCourses = Arrays.asList(course1, course2, course3);

        Historial finishedHistorial = new Historial();
        finishedHistorial.setGroupCode("MATH101");
        finishedHistorial.setStatus(HistorialStatus.FINISHED);

        Group mathGroup = new Group();
        mathGroup.setAbbreviation("MATH101");

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentIdAndStatus(studentId, HistorialStatus.FINISHED))
                .thenReturn(List.of(finishedHistorial));
        when(groupService.getGroupByGroupCode("MATH101")).thenReturn(mathGroup);

        double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

        assertEquals(40.0, percentage, 0.01);
    }

    @Test
    void getCompletedCoursesPercentage_WhenNoFinishedCourses_ShouldReturnZero() {
        String studentId = "STU008";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Science");
        student.setPlan("2024");

        Course course1 = new Course("BIO101", "Biology", 4);
        Course course2 = new Course("CHEM101", "Chemistry", 3);
        List<Course> facultyCourses = Arrays.asList(course1, course2);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentIdAndStatus(studentId, HistorialStatus.FINISHED))
                .thenReturn(List.of());

        double percentage = pemsumService.getCompletedCoursesPercentage(studentId);

        assertEquals(0.0, percentage, 0.0);
    }

    @Test
    void getStudentCoursesStatus_ShouldCombineHistorialAndPendingCourses() {
        String studentId = "STU009";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");

        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        List<Course> facultyCourses = Arrays.asList(course1, course2);

        Historial historial = new Historial();
        historial.setGroupCode("MATH101");
        historial.setStatus(HistorialStatus.FINISHED);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentId(studentId)).thenReturn(List.of(historial));

        Map<String, HistorialStatus> result = pemsumService.getStudentCoursesStatus(studentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(HistorialStatus.FINISHED, result.get("MATH101"));
        assertEquals(HistorialStatus.PENDING, result.get("PHYS101"));
    }

    @Test
    void getStudentCoursesStatus_ShouldPrioritizeNewStatus_WhenCourseAlreadyExists() {
        String studentId = "STU010";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Science");
        student.setPlan("2024");

        Course course = new Course("CHEM101", "Chemistry", 3);
        List<Course> facultyCourses = List.of(course);

        Historial historial1 = new Historial();
        historial1.setGroupCode("CHEM101");
        historial1.setStatus(HistorialStatus.ON_GOING);

        Historial historial2 = new Historial();
        historial2.setGroupCode("CHEM101");
        historial2.setStatus(HistorialStatus.FINISHED);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentId(studentId)).thenReturn(Arrays.asList(historial1, historial2));

        Map<String, HistorialStatus> result = pemsumService.getStudentCoursesStatus(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HistorialStatus.FINISHED, result.get("CHEM101"));
    }

    @Test
    void getStudentCoursesStatus_ShouldNotThrowException_WhenTryingToChangeFinishedCourse() {
        String studentId = "STU011";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");

        Course course = new Course("MATH101", "Calculus I", 4);
        List<Course> facultyCourses = List.of(course);

        Historial historial1 = new Historial();
        historial1.setGroupCode("MATH101");
        historial1.setStatus(HistorialStatus.FINISHED);

        Historial historial2 = new Historial();
        historial2.setGroupCode("MATH101");
        historial2.setStatus(HistorialStatus.FAILED);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentId(studentId)).thenReturn(Arrays.asList(historial1, historial2));

        Map<String, HistorialStatus> result = pemsumService.getStudentCoursesStatus(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HistorialStatus.FINISHED, result.get("MATH101"));
    }

    @Test
    void getStudentCoursesStatus_ShouldAllowSameFinishedStatus_WhenCourseIsAlreadyFinished() {
        String studentId = "STU012";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Science");
        student.setPlan("2024");

        Course course = new Course("BIO101", "Biology", 4);
        List<Course> facultyCourses = List.of(course);

        Historial historial1 = new Historial();
        historial1.setGroupCode("BIO101");
        historial1.setStatus(HistorialStatus.FINISHED);

        Historial historial2 = new Historial();
        historial2.setGroupCode("BIO101");
        historial2.setStatus(HistorialStatus.FINISHED);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Science", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentId(studentId)).thenReturn(Arrays.asList(historial1, historial2));

        Map<String, HistorialStatus> result = pemsumService.getStudentCoursesStatus(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(HistorialStatus.FINISHED, result.get("BIO101"));
    }

    @Test
    void getStudentCoursesStatus_WithNoHistorial_ShouldReturnAllPending() {
        String studentId = "STU013";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Engineering");
        student.setPlan("2024");

        Course course1 = new Course("MATH101", "Calculus I", 4);
        Course course2 = new Course("PHYS101", "Physics I", 3);
        List<Course> facultyCourses = Arrays.asList(course1, course2);

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024")).thenReturn(facultyCourses);
        when(historialService.getHistorialByStudentId(studentId)).thenReturn(List.of());

        Map<String, HistorialStatus> result = pemsumService.getStudentCoursesStatus(studentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(HistorialStatus.PENDING, result.get("MATH101"));
        assertEquals(HistorialStatus.PENDING, result.get("PHYS101"));
    }

    @Test
    void getStudentCoursesStatus_WhenFacultyCoursesNotFound_ShouldThrowException() {
        String studentId = "STU014";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("Unknown");
        student.setPlan("2024");

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("Unknown", "2024")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> pemsumService.getStudentCoursesStatus(studentId));
    }

    @Test
    void getCompletedCoursesPercentage_WhenTotalCreditsZero_ShouldReturnZero() {
        String studentId = "STU015";
        Student student = new Student();
        student.setUserId(studentId);
        student.setFacultyName("EmptyFaculty");
        student.setPlan("2024");

        when(studentService.getStudentById(studentId)).thenReturn(student);
        when(facultyService.findCoursesByFacultyNameAndPlan("EmptyFaculty", "2024")).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> pemsumService.getCompletedCoursesPercentage(studentId));
    }
}