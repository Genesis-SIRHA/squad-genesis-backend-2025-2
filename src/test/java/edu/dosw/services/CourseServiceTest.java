package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.repositories.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CourseService}.
 */
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_shouldReturnListOfCourses() {
        // Arrange
        Course c1 = new Course();
        c1.setAbbreviation("CS101");
        when(courseRepository.findAll()).thenReturn(List.of(c1));

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getAbbreviation());
    }

    @Test
    void getCourseById_shouldReturnCourseIfExists() {
        // Arrange
        Course c1 = new Course();
        c1.setAbbreviation("CS101");
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(c1));

        // Act
        Optional<Course> result = courseService.getCourseById("CS101");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().getAbbreviation());
    }

    @Test
    void createCourse_shouldSaveAndReturnCourse() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Algorithms", null);
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Course result = courseService.createCourse(request);

        // Assert
        assertNotNull(result);
        assertEquals("CS101", result.getAbbreviation());
        assertEquals("Algorithms", result.getCourseName());
    }

    @Test
    void createCourse_shouldThrowIfCourseExists() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Algorithms", null);
        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> courseService.createCourse(request));
    }

    @Test
    void updateCourse_shouldUpdateIfExists() {
        // Arrange
        Course existing = new Course();
        existing.setAbbreviation("CS101");
        existing.setCourseName("Old Name");

        CourseRequest request = new CourseRequest("CS101", "New Name", null);
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Optional<Course> result = courseService.updateCourse("CS101", request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getCourseName());
    }

    @Test
    void addGroupToCourse_shouldAddGroupIfCourseExists() {
        // Arrange
        Course existing = new Course();
        existing.setAbbreviation("CS101");
        when(courseRepository.findById("CS101")).thenReturn(Optional.of(existing));

        GroupRequest groupRequest = new GroupRequest(
                "G1",
                "CS",
                "2025",
                "1",
                "T1",
                true,
                1,
                30,
                0
        );

        when(groupService.createGroup(groupRequest)).thenReturn(new Group());

        // Act
        Boolean result = courseService.addGroupToCourse("CS101", groupRequest);

        // Assert
        assertTrue(result);
    }

    @Test
    void addGroupToCourse_shouldThrowIfCourseNotFound() {
        // Arrange
        when(courseRepository.findById("CS999")).thenReturn(Optional.empty());

        GroupRequest groupRequest = new GroupRequest(
                "G1", "CS", "2025", "1", "T1", true, 1, 30, 0
        );

        // Act & Assert
        assertThrows(BusinessException.class, () -> courseService.addGroupToCourse("CS999", groupRequest));
    }

    @Test
    void deleteCourse_shouldCallRepositoryDelete() {
        // Act
        courseService.deleteCourse("CS101");

        // Assert
        verify(courseRepository, times(1)).deleteById("CS101");
    }

    @Test
    void findByCode_shouldReturnCourseIfExists() {
        // Arrange
        Course c1 = new Course();
        c1.setAbbreviation("CS101");
        when(courseRepository.findByCode("CS101")).thenReturn(Optional.of(c1));

        // Act
        Optional<Course> result = courseService.findByCode("CS101");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().getAbbreviation());
    }
}
