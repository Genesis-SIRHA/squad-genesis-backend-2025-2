package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.dosw.repositories.FacultyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FacultyServiceTest {

    private FacultyRepository facultyRepository;
    private FacultyService facultyService;
    private GroupService groupService;

    @BeforeEach
    void setUp() {
        facultyRepository = mock(FacultyRepository.class);
        groupService = mock(GroupService.class);
        facultyService = new FacultyService(facultyRepository, groupService);
    }

    @Test
    void getAllCourses_shouldReturnCourses() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>());
        when(facultyRepository.findAll()).thenReturn(List.of(course));

        // Act
        List<Course> result = facultyService.getAllCourses();

        // Assert
        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).getCode());
    }

    @Test
    void getCourseById_shouldReturnResponseWhenFound() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>());
        when(facultyRepository.findById("1")).thenReturn(Optional.of(course));

        // Act
        Optional<Course> result = facultyService.getCourseById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("CS101", result.get().getCode());
    }

    @Test
    void getCourseById_shouldReturnEmptyWhenNotFound() {
        // Arrange
        when(facultyRepository.findById("missing")).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = facultyService.getCourseById("missing");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void createCourse_shouldThrowWhenCodeAlreadyExists() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Intro", null);
        when(facultyRepository.existsByCode("CS101")).thenReturn(true);

        // Act + Assert
        assertThrows(BusinessException.class, () -> facultyService.createCourse(request));
        verify(facultyRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_shouldSaveAndReturnResponse_whenNoGroupsProvided() {
        // Arrange
        CourseRequest request = new CourseRequest("CS101", "Intro", null);
        Course saved = new Course("CS101", "Intro", new ArrayList<>());
        when(facultyRepository.existsByCode("CS101")).thenReturn(false);
        when(facultyRepository.save(any(Course.class))).thenReturn(saved);

        // Act
        Course response = facultyService.createCourse(request);

        // Assert
        assertEquals("CS101", response.getCode());
        assertEquals("Intro", response.getName());
        assertNotNull(response.getGroups());
        assertTrue(response.getGroups().isEmpty());
        verify(facultyRepository).save(any(Course.class));
    }

    @Test
    void createCourse_shouldSaveAndMapGroups_whenGroupsProvided() {
        // Arrange
        GroupRequest grpReq = new GroupRequest("G1", "Prof", 20, 0);
        CourseRequest request = new CourseRequest("CS101", "Intro", List.of(grpReq));

        Course saved = new Course("CS101", "Intro", List.of(new Group("G1", "Prof", 20, 0)));

        when(facultyRepository.existsByCode("CS101")).thenReturn(false);
        when(facultyRepository.save(any(Course.class))).thenReturn(saved);

        // Act
        Course response = facultyService.createCourse(request);

        // Assert
        assertEquals("CS101", response.getCode());
        assertEquals(1, response.getGroups().size());
        assertEquals("G1", response.getGroups().get(0).getGroupCode());
    }

    @Test
    void updateCourse_shouldUpdateAndReturnResponse_whenCourseExists() {
        // Arrange
        Course existing = new Course("CS101", "OldName", new ArrayList<>());
        when(facultyRepository.findById("1")).thenReturn(Optional.of(existing));
        when(facultyRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        CourseRequest request = new CourseRequest("CS101", "NewName", null);

        // Act
        Optional<Course> updated = facultyService.updateCourse("1", request);

        // Assert
        assertTrue(updated.isPresent());
        assertEquals("NewName", updated.get().getName());
        verify(facultyRepository).save(existing);
    }

    @Test
    void updateCourse_shouldReturnEmptyWhenCourseNotFound() {
        // Arrange
        when(facultyRepository.findById("missing")).thenReturn(Optional.empty());
        CourseRequest request = new CourseRequest("CS101", "Name", null);

        // Act
        Optional<Course> updated = facultyService.updateCourse("missing", request);

        // Assert
        assertTrue(updated.isEmpty());
        verify(facultyRepository, never()).save(any(Course.class));
    }

    @Test
    void addGroupToCourse_shouldAddNewGroup() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>(List.of(new Group("G1", "Prof", 20, 0))));
        when(facultyRepository.findById("1")).thenReturn(Optional.of(course));
        when(facultyRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        GroupRequest newGroupReq = new GroupRequest("G2", "NewProf", 20, 0);

        // Act
        Optional<Course> result = facultyService.addGroupToCourse("1", newGroupReq);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getGroups().size());
        verify(facultyRepository).save(course);
    }

    @Test
    void addGroupToCourse_shouldThrowWhenGroupAlreadyExists() {
        // Arrange
        Course course = new Course("CS101", "Intro", new ArrayList<>(List.of(new Group("G1", "Prof", 20, 0))));
        when(facultyRepository.findById("1")).thenReturn(Optional.of(course));

        GroupRequest duplicate = new GroupRequest("G1", "Other", 20, 0);

        // Act + Assert
        assertThrows(BusinessException.class, () -> facultyService.addGroupToCourse("1", duplicate));
        verify(facultyRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteCourse_shouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(facultyRepository).deleteById("1");

        // Act
        facultyService.deleteCourse("1");

        // Assert
        verify(facultyRepository).deleteById("1");
    }
}

