package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.CourseResponse;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.dosw.repositories.CourseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    private CourseRepository courseRepository;
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseRepository = mock(CourseRepository.class);
        courseService = new CourseService(courseRepository);
    }

    @Test
    void getAllCourses_shouldReturnCourseResponses() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>(List.of(
                new Group("G1", "Prof", 30, 10)
        )));
        when(courseRepository.findAll()).thenReturn(List.of(course));

        // Act
        List<CourseResponse> result = courseService.getAllCourses();

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).code());
    }

    @Test
    void getCourseById_shouldReturnResponseWhenFound() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>());
        when(courseRepository.findById("1")).thenReturn(Optional.of(course));

        // Act
        Optional<CourseResponse> result = courseService.getCourseById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().code());
    }

    @Test
    void getCourseById_shouldReturnEmptyWhenNotFound() {
        // Arrange
        when(courseRepository.findById("missing")).thenReturn(Optional.empty());

        // Act
        Optional<CourseResponse> result = courseService.getCourseById("missing");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void createCourse_shouldThrowWhenCodeAlreadyExists() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Intro", null);
        when(courseRepository.existsByCode("CS101")).thenReturn(true);

        // Act + Assert
        assertThrows(BusinessException.class, () -> courseService.createCourse(request));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_shouldSaveAndReturnResponse_whenNoGroupsProvided() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Intro", null);
        Course saved = new Course("CS101", "Intro", new ArrayList<>());
        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        // Act
        CourseResponse response = courseService.createCourse(request);

        // Assert
        assertEquals("CS101", response.code());
        assertEquals("Intro", response.name());
        assertNotNull(response.groups());
        assertTrue(response.groups().isEmpty());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_shouldSaveAndMapGroups_whenGroupsProvided() {
        // Arrange
        GroupRequest grpReq = new GroupRequest("G1", "Prof", 20, 0);
        CourseRequest request = new CourseRequest("CS101", "Intro", List.of(grpReq));

        Course saved = new Course("CS101", "Intro", new ArrayList<>(List.of(
                new Group("G1", "Prof", 20, 0)
        )));

        when(courseRepository.existsByCode("CS101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(saved);

        // Act
        CourseResponse response = courseService.createCourse(request);

        // Assert
        assertEquals("CS101", response.code());
        assertEquals(1, response.groups().size());
        assertEquals("G1", response.groups().get(0).getGroupCode());
    }

    @Test
    void updateCourse_shouldUpdateAndReturnResponse_whenCourseExists() {
        // Arrange
        Course existing = new Course("CS101", "OldName", new ArrayList<>());
        when(courseRepository.findById("1")).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            // simulate save returning the updated course instance
            return inv.getArgument(0);
        });

        CourseRequest request = new CourseRequest("CS101", "NewName", null);

        // Act
        Optional<CourseResponse> updated = courseService.updateCourse("1", request);

        // Assert
        assertTrue(updated.isPresent());
        assertEquals("NewName", updated.get().name());
        verify(courseRepository).save(existing);
    }

    @Test
    void updateCourse_shouldReturnEmptyWhenCourseNotFound() {
        // Arrange
        when(courseRepository.findById("missing")).thenReturn(Optional.empty());
        CourseRequest request = new CourseRequest("CS101", "Name", null);

        // Act
        Optional<CourseResponse> updated = courseService.updateCourse("missing", request);

        // Assert
        assertTrue(updated.isEmpty());
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void addGroupToCourse_shouldAddNewGroup() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>(List.of(
                new Group("G1", "Prof", 10, 5)
        )));
        when(courseRepository.findById("1")).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        GroupRequest newGroupReq = new GroupRequest("G2", "NewProf", 20, 0);

        // Act
        Optional<CourseResponse> result = courseService.addGroupToCourse("1", newGroupReq);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().groups().size());
        // verify that repository save was called with the same course instance
        verify(courseRepository).save(course);
    }

    @Test
    void addGroupToCourse_shouldThrowWhenGroupAlreadyExists() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>(List.of(
                new Group("G1", "Prof", 10, 5)
        )));
        when(courseRepository.findById("1")).thenReturn(Optional.of(course));

        GroupRequest duplicate = new GroupRequest("G1", "Other", 20, 0);

        // Act + Assert
        assertThrows(BusinessException.class, () -> courseService.addGroupToCourse("1", duplicate));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourse_shouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(courseRepository).deleteById("1");

        // Act
        courseService.deleteCourse("1");

        // Assert
        verify(courseRepository).deleteById("1");
    }
}
