package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.CoursesDto;
import edu.dosw.dto.FacultyDto;
import edu.dosw.dto.UpdateCourseDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.repositories.FacultyRepository;
import edu.dosw.services.Validators.FacultyValidator;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyValidator facultyValidator;

    @InjectMocks
    private FacultyService facultyService;

    @Test
    void createFaculty_WithValidData_ShouldReturnSavedFaculty() {
        FacultyDto facultyDto = createFacultyDto();
        Faculty faculty = createFaculty();

        doNothing().when(facultyValidator).validateAddCourses(any(CoursesDto.class));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        Faculty result = facultyService.createFaculty(facultyDto);

        assertNotNull(result);
        verify(facultyValidator).validateAddCourses(any(CoursesDto.class));
        verify(facultyRepository).save(any(Faculty.class));
    }

    @Test
    void createFaculty_WithNullCourses_ShouldReturnSavedFaculty() {
        FacultyDto facultyDto = new FacultyDto("Engineering", "2024", new ArrayList<>());
        Faculty faculty = createFaculty();

        doNothing().when(facultyValidator).validateAddCourses(any(CoursesDto.class));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculty);

        Faculty result = facultyService.createFaculty(facultyDto);

        assertNotNull(result);
        verify(facultyValidator).validateAddCourses(any(CoursesDto.class));
        verify(facultyRepository).save(any(Faculty.class));
    }

    @Test
    void getAllFacultyNames_ShouldReturnFacultyNamesMap() {
        List<Faculty> faculties = Arrays.asList(
                createFaculty("Engineering", "2024"),
                createFaculty("Science", "2024")
        );

        when(facultyRepository.findAll()).thenReturn(faculties);

        Map<String, String> result = facultyService.getAllFacultyNames();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("engineering"));
        assertTrue(result.containsKey("science"));
        assertEquals("2024", result.get("engineering"));
        verify(facultyRepository).findAll();
    }

    @Test
    void getAllFaculties_ShouldReturnAllFaculties() {
        List<Faculty> expectedFaculties = Arrays.asList(
                createFaculty("Engineering", "2024"),
                createFaculty("Science", "2024")
        );

        when(facultyRepository.findAll()).thenReturn(expectedFaculties);

        List<Faculty> result = facultyService.getAllFaculties();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(facultyRepository).findAll();
    }

    @Test
    void getAllFaculties_WhenRepositoryThrowsException_ShouldThrowBusinessException() {
        when(facultyRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> facultyService.getAllFaculties()
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred"));
        verify(facultyRepository).findAll();
    }

    @Test
    void getFacultyByNameAndPlan_WithExistingFaculty_ShouldReturnFaculty() {
        Faculty expectedFaculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(expectedFaculty));

        Faculty result = facultyService.getFacultyByNameAndPlan("Engineering", "2024");

        assertNotNull(result);
        assertEquals("Engineering", result.getFacultyName());
        assertEquals("2024", result.getPlan());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
    }

    @Test
    void getFacultyByNameAndPlan_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        when(facultyRepository.findByNameAndPlan("NonExistent", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.getFacultyByNameAndPlan("NonExistent", "2024")
        );

        assertEquals("Faculty not found: NonExistent", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("NonExistent", "2024");
    }

    @Test
    void updateFacultyByNameAndPlan_WithExistingFaculty_ShouldReturnUpdatedFaculty() {
        FacultyDto facultyDto = createFacultyDto();
        Faculty existingFaculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(existingFaculty));
        doNothing().when(facultyValidator).validateAddCourses(any(CoursesDto.class));
        when(facultyRepository.save(existingFaculty)).thenReturn(existingFaculty);

        Faculty result = facultyService.updateFacultyByNameAndPlan(facultyDto);

        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyValidator).validateAddCourses(any(CoursesDto.class));
        verify(facultyRepository).save(existingFaculty);
    }

    @Test
    void updateFacultyByNameAndPlan_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        FacultyDto facultyDto = createFacultyDto();

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.updateFacultyByNameAndPlan(facultyDto)
        );

        assertEquals("Faculty not found: Engineering", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void updateFacultyByNameAndPlan_WhenSaveFails_ShouldThrowBusinessException() {
        FacultyDto facultyDto = createFacultyDto();
        Faculty existingFaculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(existingFaculty));
        doNothing().when(facultyValidator).validateAddCourses(any(CoursesDto.class));
        when(facultyRepository.save(existingFaculty)).thenThrow(new RuntimeException("Save failed"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> facultyService.updateFacultyByNameAndPlan(facultyDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred"));
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyValidator).validateAddCourses(any(CoursesDto.class));
        verify(facultyRepository).save(existingFaculty);
    }

    @Test
    void addCoursesToPlan_WithExistingFaculty_ShouldReturnUpdatedFaculty() {
        FacultyDto facultyDto = createFacultyDto();
        Faculty existingFaculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(existingFaculty));
        when(facultyRepository.save(existingFaculty)).thenReturn(existingFaculty);

        Faculty result = facultyService.addCoursesToPlan(facultyDto);

        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyRepository).save(existingFaculty);
    }

    @Test
    void addCoursesToPlan_WithNonExistingFaculty_ShouldThrowBusinessException() {
        FacultyDto facultyDto = createFacultyDto();

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> facultyService.addCoursesToPlan(facultyDto)
        );

        assertEquals("Faculty not found: Engineering", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void addCoursesToPlan_WhenSaveFails_ShouldThrowBusinessException() {
        FacultyDto facultyDto = createFacultyDto();
        Faculty existingFaculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(existingFaculty));
        when(facultyRepository.save(existingFaculty)).thenThrow(new RuntimeException("Save failed"));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> facultyService.addCoursesToPlan(facultyDto)
        );

        assertTrue(exception.getMessage().contains("An inesperated error has occurred"));
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
        verify(facultyRepository).save(existingFaculty);
    }

    @Test
    void findCoursesByFacultyNameAndPlan_WithExistingFaculty_ShouldReturnCourses() {
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("Engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        List<Course> result = facultyService.findCoursesByFacultyNameAndPlan("Engineering", "2024");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(facultyRepository).findByNameAndPlan("Engineering", "2024");
    }

    @Test
    void findCoursesByFacultyNameAndPlan_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        when(facultyRepository.findByNameAndPlan("NonExistent", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.findCoursesByFacultyNameAndPlan("NonExistent", "2024")
        );

        assertEquals("Faculty not found: NonExistent", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("NonExistent", "2024");
    }

    @Test
    void addCourse_WithValidData_ShouldReturnUpdatedFaculty() {
        CourseRequest request = new CourseRequest("NEW101", "New Course", 3, "Engineering", "2024", "1", null);
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(faculty)).thenReturn(faculty);

        Faculty result = facultyService.addCourse(request);

        assertNotNull(result);
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }

    @Test
    void addCourse_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        CourseRequest request = new CourseRequest("CS101", "Computer Science", 3, "Engineering", "2024", "1", null);

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.addCourse(request)
        );

        assertEquals("Faculty not found: Engineering", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void addCourse_WithExistingCourse_ShouldThrowResourceAlreadyExistsException() {
        CourseRequest request = new CourseRequest("CS101", "Computer Science", 3, "Engineering", "2024", "1", null);
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> facultyService.addCourse(request)
        );

        assertEquals("Course already exists: CS101", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void updateCourse_WithValidData_ShouldReturnUpdatedCourse() {
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO("Advanced Computer Science", 4);
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(faculty)).thenReturn(faculty);

        Course result = facultyService.updateCourse("CS101", "Engineering", "2024", updateCourseDTO);

        assertNotNull(result);
        assertEquals("advanced computer science", result.getCourseName());
        assertEquals(4, result.getCredits());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }

    @Test
    void updateCourse_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO("Advanced Computer Science", 4);

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.updateCourse("CS101", "Engineering", "2024", updateCourseDTO)
        );

        assertEquals("Faculty not found: Engineering", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void updateCourse_WithNonExistingCourse_ShouldThrowResourceNotFoundException() {
        UpdateCourseDTO updateCourseDTO = new UpdateCourseDTO("Advanced Computer Science", 4);
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.updateCourse("NONEXISTENT", "Engineering", "2024", updateCourseDTO)
        );

        assertEquals("Course not found: NONEXISTENT", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void findCourseByAbbreviation_WithExistingCourse_ShouldReturnCourse() {
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        Course result = facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024");

        assertNotNull(result);
        assertEquals("CS101", result.getAbbreviation());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void findCourseByAbbreviation_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024")
        );

        assertEquals("Course not found: CS101", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void findCourseByAbbreviation_WithEmptyCourses_ShouldThrowResourceNotFoundException() {
        Faculty faculty = createFaculty("Engineering", "2024");
        faculty.setCourses(new ArrayList<>());

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.findCourseByAbbreviation("CS101", "Engineering", "2024")
        );

        assertEquals("Course data is corrupted for: CS101", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void findCourseByAbbreviation_WithNonExistingCourse_ShouldReturnNull() {
        Faculty faculty = createFaculty("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));

        Course result = facultyService.findCourseByAbbreviation("NONEXISTENT", "Engineering", "2024");

        assertNull(result);
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
    }

    @Test
    void deleteCourse_WithExistingCourse_ShouldDeleteCourse() {
        Faculty faculty = createFacultyWithMutableCourses("Engineering", "2024");

        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.of(faculty));
        when(facultyRepository.save(faculty)).thenReturn(faculty);

        facultyService.deleteCourse("CS101", "Engineering", "2024");

        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository).save(faculty);
    }

    @Test
    void deleteCourse_WithNonExistingFaculty_ShouldThrowResourceNotFoundException() {
        when(facultyRepository.findByNameAndPlan("engineering", "2024"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> facultyService.deleteCourse("CS101", "Engineering", "2024")
        );

        assertEquals("Faculty not found: Engineering", exception.getMessage());
        verify(facultyRepository).findByNameAndPlan("engineering", "2024");
        verify(facultyRepository, never()).save(any());
    }

    private FacultyDto createFacultyDto() {
        List<Course> courses = Arrays.asList(
                createCourse("CS101", "Computer Science", 3, "1"),
                createCourse("MATH101", "Mathematics", 4, "1")
        );
        return new FacultyDto("Engineering", "2024", courses);
    }

    private Faculty createFaculty() {
        return createFaculty("Engineering", "2024");
    }

    private Faculty createFaculty(String name, String plan) {
        Faculty faculty = new Faculty();
        faculty.setFacultyName(name);
        faculty.setPlan(plan);

        List<Course> courses = Arrays.asList(
                createCourse("CS101", "Computer Science", 3, "1"),
                createCourse("MATH101", "Mathematics", 4, "1")
        );
        faculty.setCourses(new ArrayList<>(courses));

        return faculty;
    }

    private Faculty createFacultyWithMutableCourses(String name, String plan) {
        Faculty faculty = new Faculty();
        faculty.setFacultyName(name);
        faculty.setPlan(plan);

        List<Course> courses = new ArrayList<>(Arrays.asList(
                createCourse("CS101", "Computer Science", 3, "1"),
                createCourse("MATH101", "Mathematics", 4, "1")
        ));
        faculty.setCourses(courses);

        return faculty;
    }

    private Course createCourse(String abbreviation, String courseName, int credits, String semester) {
        Course course = new Course();
        course.setAbbreviation(abbreviation);
        course.setCourseName(courseName);
        course.setCredits(credits);
        course.setSemester(semester);
        return course;
    }
}