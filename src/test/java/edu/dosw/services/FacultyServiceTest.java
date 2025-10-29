package edu.dosw.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.repositories.FacultyRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

  @Mock private FacultyRepository facultyRepository;

  @InjectMocks private FacultyService facultyService;

  private Faculty faculty;
  private Course course;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    Course course = new Course("CS101", "Algorithms", 4, "1", List.of());
    Faculty faculty = new Faculty("Engineering", "2025", List.of(course));
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
