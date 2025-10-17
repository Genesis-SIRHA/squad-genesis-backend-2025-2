package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.FacultyDto;
import edu.dosw.dto.UpdateCourseDTO;
import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceAlreadyExistsException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.repositories.FacultyRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to courses and groups. Provides methods for
 * CRUD operations on courses and their associated groups.
 */
@AllArgsConstructor
@Service
public class FacultyService {
  private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
  private final FacultyRepository facultyRepository;

  public Faculty createFaculty(FacultyDto facultyDto) {
    Faculty faculty = new Faculty();
    faculty.setFacultyName(facultyDto.facultyName());
    faculty.setPlan(facultyDto.plan());
    if (facultyDto.courses() != null) faculty.setCourses(facultyDto.courses());
    return facultyRepository.save(faculty);
  }

  public Map<String, String> getAllFacultyNames() {
    Map<String, String> facultyInfo = new HashMap<>();
    List<Faculty> faculties = facultyRepository.findAll();
    for (Faculty faculty : faculties) {
      logger.info("Faculty processed: " + faculty.getFacultyName());
      facultyInfo.put(faculty.getFacultyName().toLowerCase(), faculty.getPlan());
    }
    return facultyInfo;
  }

  public List<Faculty> getAllFaculties() {
    try {
      return facultyRepository.findAll();
    } catch (Exception e) {
      logger.error(
          "An inesperated error has occurred when getting all faculties: {}", e.getMessage());
      throw new BusinessException(
          "An inesperated error has occurred when getting all faculties: " + e.getMessage());
    }
  }

  public Faculty getFacultyByNameAndPlan(String name, String plan) {
    Faculty faculty = facultyRepository.findByNameAndPlan(name, plan).orElse(null);
    if (faculty == null) {
      logger.error("Faculty not found: " + name);
      throw new ResourceNotFoundException("Faculty not found: " + name);
    }
    return faculty;
  }

  public Faculty updateFacultyByNameAndPlan(FacultyDto facultyDto) {
    Faculty faculty =
        facultyRepository
            .findByNameAndPlan(facultyDto.facultyName(), facultyDto.plan())
            .orElse(null);
    if (faculty == null) {
      logger.error("Faculty not found: {}", facultyDto.facultyName());
      throw new ResourceNotFoundException("Faculty not found: " + facultyDto.facultyName());
    }
    faculty.setFacultyName(facultyDto.facultyName());
    faculty.setPlan(facultyDto.plan());
    if (facultyDto.courses() != null) faculty.setCourses(facultyDto.courses());
    try {
      return facultyRepository.save(faculty);
    } catch (Exception e) {
      logger.error(
          "An inesperated error has occurred when updating the faculty: {}", e.getMessage());
      throw new BusinessException(
          "An inesperated error has occurred when updating the faculty: " + e.getMessage());
    }
  }

  public List<Course> findCoursesByFacultyNameAndPlan(String facultyName, String plan) {
    Faculty faculty = getFacultyByNameAndPlan(facultyName, plan);
    if (faculty == null) {
      logger.error("Faculty not found: " + facultyName);
      throw new ResourceNotFoundException("Faculty not found: " + facultyName);
    }
    return new ArrayList<>(faculty.getCourses());
  }

  /**
   * Creates a new course with the provided details.
   *
   * @param request The course details to create
   * @return The created course
   * @throws BusinessException if a course with the same abbreviation already exists
   */
  public Faculty addCourse(CourseRequest request) {
    Faculty faculty =
        facultyRepository
            .findByNameAndPlan(request.facultyName().toLowerCase(), request.plan())
            .orElseThrow(
                () -> new ResourceNotFoundException("Faculty not found: " + request.facultyName()));

    if (faculty.getCourses().stream()
        .anyMatch(c -> c.getAbbreviation().equals(request.abbreviation().toUpperCase()))) {
      logger.error("Course already exists: " + request.abbreviation());
      throw new ResourceAlreadyExistsException("Course already exists: " + request.abbreviation());
    }

    Course course = request.toEntity();
    faculty.getCourses().add(course);

    return facultyRepository.save(faculty);
  }

  /**
   * Updates an existing course with new details.
   *
   * @param courseAbbreviation The course abbreviation to update
   * @param facultyName The faculty name of the faculty
   * @param plan The plan of the faculty
   * @param updateCourseDTO The updated course details
   * @return An Optional containing the updated Course if found, or empty if not found
   */
  public Course updateCourse(
      String courseAbbreviation, String facultyName, String plan, UpdateCourseDTO updateCourseDTO) {
    Faculty faculty =
        facultyRepository
            .findByNameAndPlan(facultyName.toLowerCase(), plan)
            .orElseThrow(() -> new ResourceNotFoundException("Faculty not found: " + facultyName));

    Course existingCourse =
        faculty.getCourses().stream()
            .filter(c -> c.getAbbreviation().equals(courseAbbreviation.toUpperCase()))
            .findFirst()
            .orElseThrow(
                () -> new ResourceNotFoundException("Course not found: " + courseAbbreviation));

    if (updateCourseDTO.courseName() != null)
      existingCourse.setCourseName(updateCourseDTO.courseName().toLowerCase());
    if (updateCourseDTO.credits() != null) existingCourse.setCredits(updateCourseDTO.credits());

    facultyRepository.save(faculty);
    return existingCourse;
  }

  /**
   * Finds a course by its abbreviation.
   *
   * @param courseAbbreviation The course abbreviation to search for
   * @param facultyName The faculty name of the faculty
   * @param plan The plan of the faculty
   * @return An Optional containing the Course if found, or throws BusinessException if not found
   * @throws BusinessException if the course is not found or no faculties exist
   */
  public Course findCourseByAbbreviation(
      String courseAbbreviation, String facultyName, String plan) {
    Faculty faculty =
        facultyRepository.findByNameAndPlan(facultyName.toLowerCase(), plan).orElse(null);

    if (faculty == null) {
      logger.error("Course not found: {} ", courseAbbreviation);
      throw new ResourceNotFoundException("Course not found: " + courseAbbreviation);
    }

    List<Course> courses = faculty.getCourses();
    if (courses == null || courses.isEmpty()) {
      logger.error("No courses found for code: {}", courseAbbreviation);
      throw new ResourceNotFoundException("Course data is corrupted for: " + courseAbbreviation);
    }

    return courses.stream()
        .filter(c -> c.getAbbreviation().equals(courseAbbreviation.toUpperCase()))
        .findFirst()
        .orElse(null);
  }

  /**
   * Deletes a course by its abbreviation.
   *
   * @param courseAbbreviation The abbreviation of the course to delete
   * @param facultyName The faculty name of the faculty
   * @param plan The plan of the faculty
   */
  public void deleteCourse(String courseAbbreviation, String facultyName, String plan) {
    Faculty faculty =
        facultyRepository.findByNameAndPlan(facultyName.toLowerCase(), plan).orElse(null);
    if (faculty == null) {
      logger.error("Faculty not found: " + facultyName);
      throw new ResourceNotFoundException("Faculty not found: " + facultyName);
    }
    faculty
        .getCourses()
        .removeIf(c -> c.getAbbreviation().equals(courseAbbreviation.toUpperCase()));
    facultyRepository.save(faculty);
  }
}
