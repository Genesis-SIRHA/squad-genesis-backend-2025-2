package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.*;

import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for generating and managing student academic records (Pemsum). This
 * service coordinates between different services to build a comprehensive academic summary.
 */
@Service
public class PemsumService {
  private final FacultyService facultyService;
  private final StudentService studentService;
  private final HistorialService historialService;
  private final PeriodService periodService;
  private static final Logger logger = LoggerFactory.getLogger(PemsumService.class);

  /**
   * Constructs a new PemsumService with required dependencies.
   *
   * @param facultyService Service for faculty-related operations
   * @param studentService Service for student-related operations
   * @param historialService Service for academic history operations
   */
  @Autowired
  public PemsumService(
      FacultyService facultyService,
      StudentService studentService,
      HistorialService historialService) {
    this.facultyService = facultyService;
    this.historialService = historialService;
    this.studentService = studentService;
    Clock clock = Clock.systemDefaultZone();
    this.periodService = new PeriodService(clock);
  }

  /**
   * Retrieves the academic record (Pemsum) for a specific student.
   *
   * @param studentId The unique identifier of the student
   * @return A Pemsum object containing the student's academic summary
   */
  public Pemsum getPemsum(String studentId) {
    return buildPemsum(studentId);
  }

  /**
   * Builds a Pemsum object by gathering and processing student academic data.
   *
   * @param studentId The unique identifier of the student
   * @return A fully constructed Pemsum object
   * @throws BusinessException if faculty fullName or plan is invalid
   */
  private Pemsum buildPemsum(String studentId) {
    Student student = studentService.getStudentById(studentId);
    String facultyName = student.getFacultyName();
    String plan = student.getPlan();

    List<Course> courses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);
    if (courses.isEmpty()) {
      logger.error("Invalid faculty fullName or plan: " + facultyName + " - " + plan);
      throw new BusinessException("Invalid faculty fullName or plan: " + facultyName + " - " + plan);
    }

    String year = periodService.getYear();
    String period = periodService.getPeriod();

    List<Historial> historials = historialService.getSessionsByCourses(studentId, courses);

    Map<Course, String> coursesMap = getCoursesMap(courses, historials);

    int totalCredits = courses.stream().mapToInt(Course::getCredits).sum();
    int approvedCredits = getApprovedCredits(coursesMap);

    return new Pemsum.Builder()
        .studentId(studentId)
        .studentName(student.getFullName())
        .facultyName(facultyName)
        .facultyPlan(plan)
        .totalCredits(totalCredits)
        .approvedCredits(approvedCredits)
        .courses(coursesMap)
        .build();
  }

  /**
   * Calculates the total number of approved credits from a course map.
   *
   * @param coursesMap Map of courses with their status
   * @return The sum of credits for all approved courses
   */
  private int getApprovedCredits(Map<Course, String> coursesMap) {
    int approvedCredits = 0;
    for (Course course : coursesMap.keySet()) {
      if ("approved".equalsIgnoreCase(coursesMap.get(course))) {
        approvedCredits += course.getCredits();
      }
    }
    return approvedCredits;
  }

  /**
   * Creates a map of courses with their corresponding status based on academic history.
   *
   * @param courses List of all courses in the student's program
   * @param historials List of the student's academic history records
   * @return A map of courses with their current status
   */
  private Map<Course, String> getCoursesMap(List<Course> courses, List<Historial> historials) {
    Map<Course, String> coursesMap = new HashMap<>();
    for (Course course : courses) {
      historials.stream()
          .filter(h -> h.getGroupCode().equals(course.getAbbreviation()))
          .findFirst()
          .ifPresentOrElse(
              h -> coursesMap.put(course, h.getStatus()), () -> coursesMap.put(course, "pending"));
    }
    return coursesMap;
  }
}
