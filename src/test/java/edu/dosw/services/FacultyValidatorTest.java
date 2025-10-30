package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.dto.CoursesDto;
import edu.dosw.model.Course;
import java.util.List;
import java.util.Map;

import edu.dosw.services.Validators.FacultyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FacultyValidatorTest {

    @InjectMocks
    private FacultyValidator facultyValidator;

    @Test
    void validateAddCourses_WithValidFirstSemesterCourses_ShouldNotThrowException() {
        Course course1 = new Course();
        course1.setAbbreviation("MATH101");
        course1.setRequisites(List.of());

        Course course2 = new Course();
        course2.setAbbreviation("PHYS101");
        course2.setRequisites(List.of());

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(course1, course2)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithFirstSemesterCoursesHavingRequisites_ShouldThrowException() {
        Course course1 = new Course();
        course1.setAbbreviation("MATH101");
        course1.setRequisites(List.of());

        Course course2 = new Course();
        course2.setAbbreviation("PHYS101");
        course2.setRequisites(List.of("MATH101")); // Invalid: first semester course with requisites

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(course1, course2)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facultyValidator.validateAddCourses(coursesDto)
        );

        assertEquals("The first Semester classes cannot have requisites", exception.getMessage());
    }

    @Test
    void validateAddCourses_WithValidMultipleSemesters_ShouldNotThrowException() {
        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        Course semester1Course2 = new Course();
        semester1Course2.setAbbreviation("PHYS101");
        semester1Course2.setRequisites(List.of());

        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("MATH102");
        semester2Course1.setRequisites(List.of("MATH101"));

        Course semester2Course2 = new Course();
        semester2Course2.setAbbreviation("PHYS102");
        semester2Course2.setRequisites(List.of("PHYS101"));

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(semester1Course1, semester1Course2),
                "2", List.of(semester2Course1, semester2Course2)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithInvalidRequisitesInLaterSemester_ShouldThrowException() {
        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("MATH102");
        semester2Course1.setRequisites(List.of("MATH101", "INVALID101")); // Invalid requisite

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(semester1Course1),
                "2", List.of(semester2Course1)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facultyValidator.validateAddCourses(coursesDto)
        );

        assertTrue(exception.getMessage().contains("has requisites that are not in previous courses"));
        assertTrue(exception.getMessage().contains("INVALID101"));
    }

    @Test
    void validateAddCourses_WithMultipleInvalidRequisites_ShouldThrowExceptionWithAllInvalidRequisites() {
        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("ADVANCED101");
        semester2Course1.setRequisites(List.of("MATH101", "INVALID101", "INVALID102"));

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(semester1Course1),
                "2", List.of(semester2Course1)
        ));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> facultyValidator.validateAddCourses(coursesDto)
        );

        assertTrue(exception.getMessage().contains("INVALID101"));
        assertTrue(exception.getMessage().contains("INVALID102"));
    }

    @Test
    void validateAddCourses_WithEmptyCourses_ShouldNotThrowException() {
        CoursesDto coursesDto = new CoursesDto(Map.of());

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithSemestersOutOfOrderButValidRequisites_ShouldNotThrowException() {
        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("MATH102");
        semester2Course1.setRequisites(List.of("MATH101"));

        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        // Semesters out of order in the map
        CoursesDto coursesDto = new CoursesDto(Map.of(
                "2", List.of(semester2Course1),
                "1", List.of(semester1Course1)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithComplexDependencyChain_ShouldNotThrowException() {
        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        Course semester1Course2 = new Course();
        semester1Course2.setAbbreviation("PHYS101");
        semester1Course2.setRequisites(List.of());

        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("MATH102");
        semester2Course1.setRequisites(List.of("MATH101"));

        Course semester2Course2 = new Course();
        semester2Course2.setAbbreviation("PHYS102");
        semester2Course2.setRequisites(List.of("PHYS101"));

        Course semester3Course1 = new Course();
        semester3Course1.setAbbreviation("ADVANCED201");
        semester3Course1.setRequisites(List.of("MATH102", "PHYS102"));

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(semester1Course1, semester1Course2),
                "2", List.of(semester2Course1, semester2Course2),
                "3", List.of(semester3Course1)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithCrossSemesterDependencies_ShouldNotThrowException() {
        Course semester1Course1 = new Course();
        semester1Course1.setAbbreviation("MATH101");
        semester1Course1.setRequisites(List.of());

        Course semester2Course1 = new Course();
        semester2Course1.setAbbreviation("PHYS101");
        semester2Course1.setRequisites(List.of("MATH101"));

        Course semester2Course2 = new Course();
        semester2Course2.setAbbreviation("CALC101");
        semester2Course2.setRequisites(List.of("MATH101"));

        Course semester3Course1 = new Course();
        semester3Course1.setAbbreviation("ADVANCED201");
        semester3Course1.setRequisites(List.of("PHYS101", "CALC101"));

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(semester1Course1),
                "2", List.of(semester2Course1, semester2Course2),
                "3", List.of(semester3Course1)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }

    @Test
    void validateAddCourses_WithSingleSemesterAndNoRequisites_ShouldNotThrowException() {
        Course course1 = new Course();
        course1.setAbbreviation("INTRO101");
        course1.setRequisites(List.of());

        Course course2 = new Course();
        course2.setAbbreviation("BASIC101");
        course2.setRequisites(List.of());

        CoursesDto coursesDto = new CoursesDto(Map.of(
                "1", List.of(course1, course2)
        ));

        assertDoesNotThrow(() -> facultyValidator.validateAddCourses(coursesDto));
    }
}