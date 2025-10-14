package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.FacultyDto;
import edu.dosw.dto.UpdateCourseDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.repositories.FacultyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyService facultyService;

    @Test
    void createFaculty_WithValidData_ShouldReturnSavedFaculty() {
        // Given
        FacultyDto facultyDto = new FacultyDto("Engineering", "2024", new ArrayList<>());
        Faculty faculty = new Faculty();
        faculty.setFacultyName("Engineering");
        faculty.setPlan("2024");
        faculty.setCourses(new ArrayList<>());

        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        // When
        Faculty result = facultyService.createFaculty(facultyDto);

        // Then
        assertNotNull(result);
        assertEquals("Engineering", result.getFacultyName());
        verify(facultyRepository).save(any(Faculty.class));
    }

    @Test
    void getAllFacultyNames_ShouldReturnMapWithFacultyNamesAndPlans() {
        // Given
        List<Faculty> faculties = Arrays.asList(
                createFaculty("Engineering", "2024"),
                createFaculty("Medicine", "2024")
        );

        when(facultyRepository.findAll()).thenReturn(faculties);

        // When
        Map<String, String> result = facultyService.getAllFacultyNames();

        // Then
        assertEquals(2, result.size());
        assertEquals("2024", result.get("engineering"));
        assertEquals("2024", result.get("medicine"));
        verify(facultyRepository).findAll();
    }

    @Test
    void getAllFaculties_ShouldReturnAllFaculties() {
        // Given
        List<Faculty> faculties = Arrays.asList(
                createFaculty("Engineering", "2024"),
                createFaculty("Medicine", "2024")
        );

        when(facultyRepository.findAll()).thenReturn(faculties);

        // When
        List<Faculty> result = facultyService.getAllFaculties();

        // Then
        assertEquals(2, result.size());
        verify(facultyRepository).findAll();
    }

    @Test
    void getAllFaculties_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        // Given
        when(facultyRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> facultyService.getAllFaculties());

        assertTrue(exception.getMessage().contains("An inesperated error has occurred when getting all faculties"));
        verify(facultyRepository).findAll();
    }

    @Test
    void getFacultyByNameAndPlan_WithExistingFaculty_ShouldReturnFaculty() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        // When
        Faculty result = facultyService.getFacultyByNameAndPlan("Engineering", "2024");

        // Then
        assertNotNull(result);
        assertEquals("Engineering", result.getFacultyName());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
    }

    @Test
    void getFacultyByNameAndPlan_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        // Given
        when(facultyRepository.findByNameAndPlan("NonExisting", "2024"))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> facultyService.getFacultyByNameAndPlan("NonExisting", "2024"));

        assertEquals("Faculty not found: NonExisting", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("NonExisting", "2024");
    }

    @Test
    void updateFacultyByNameAndPlan_WithExistingFaculty_ShouldReturnUpdatedFaculty() {
        // Given
        Faculty existingFaculty = createFaculty("Engineering", "2024");
        FacultyDto facultyDto = new FacultyDto("Engineering", "2024", Arrays.asList(createCourse("MATH101")));

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(existingFaculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(existingFaculty);

        // When
        Faculty result = facultyService.updateFacultyByNameAndPlan(facultyDto);

        // Then
        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyRepository).save(existingFaculty);
    }

    @Test
    void updateFacultyByNameAndPlan_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        // Given
        FacultyDto facultyDto = new FacultyDto("NonExisting", "2024", new ArrayList<>());
        when(facultyRepository.findByNameAndPlan("NonExisting", "2024"))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> facultyService.updateFacultyByNameAndPlan(facultyDto));

        assertEquals("Faculty not found: NonExisting", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("NonExisting", "2024");
    }

    @Test
    void findCoursesByFacultyNameAndPlan_WithExistingFaculty_ShouldReturnCourses() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        faculty.setCourses(Arrays.asList(createCourse("MATH101"), createCourse("PHYS101")));

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        // When
        List<Course> result = facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024");

        // Then
        assertEquals(2, result.size());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
    }

    @Test
    void addCourse_WithValidData_ShouldReturnUpdatedFaculty() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        faculty.setCourses(new ArrayList<>());

        CourseRequest courseRequest = new CourseRequest(
                "Mathematics", "MATH101", 3, "Engineering", "2024"
        );

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        // When
        Faculty result = facultyService.addCourse(courseRequest);

        // Then
        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }


    @Test
    void updateCourse_WithExistingCourse_ShouldReturnUpdatedCourse() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        Course existingCourse = createCourse("MATH101");
        faculty.setCourses(Arrays.asList(existingCourse));

        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO("Advanced Mathematics", 4);

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        // When
        Course result = facultyService.updateCourse("MATH101", "Engineering", "2024", updateCourseDTO);

        // Then
        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }

    @Test
    void findCourseByAbbreviation_WithExistingCourse_ShouldReturnCourse() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        Course expectedCourse = createCourse("MATH101");
        faculty.setCourses(Arrays.asList(expectedCourse));

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        // When
        Course result = facultyService.findCourseByAbbreviation("MATH101", "Engineering", "2024");

        // Then
        assertNotNull(result);
        assertEquals("MATH101", result.getAbbreviation());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void findCourseByAbbreviation_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        // Given
        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> facultyService.findCourseByAbbreviation("MATH101", "Engineering", "2024"));

        assertEquals("Course not found: MATH101", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void deleteCourse_WithExistingCourse_ShouldRemoveCourse() {
        // Given
        Faculty faculty = createFaculty("Engineering", "2024");
        faculty.setCourses(new ArrayList<>(Arrays.asList(createCourse("MATH101"))));

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        // When
        facultyService.deleteCourse("MATH101", "Engineering", "2024");

        // Then
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }

    // Helper methods
    private Faculty createFaculty(String name, String plan) {
        Faculty faculty = new Faculty();
        faculty.setFacultyName(name);
        faculty.setPlan(plan);
        faculty.setCourses(new ArrayList<>());
        return faculty;
    }

    private Course createCourse(String abbreviation) {
        Course course = new Course();
        course.setCourseName(abbreviation.toLowerCase() + " Course");
        course.setAbbreviation(abbreviation);
        course.setCredits(3);
        return course;
    }
}