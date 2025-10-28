package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.*;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.services.UserServices.StudentService;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final GroupService groupService;
    private final PeriodService periodService;
    private static final Logger logger = LoggerFactory.getLogger(PemsumService.class);

    /**
     * Constructs a new PemsumService with required dependencies.
     *
     * @param facultyService   Service for faculty-related operations
     * @param studentService   Service for student-related operations
     * @param historialService Service for academic history operations
     * @param groupService     Service for group-related operations
     */
    @Autowired
    public PemsumService(
            FacultyService facultyService,
            StudentService studentService,
            HistorialService historialService,
            GroupService groupService) {
        this.facultyService = facultyService;
        this.historialService = historialService;
        this.studentService = studentService;
        this.groupService = groupService;
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
            throw new ResourceNotFoundException(
                    "Invalid faculty fullName or plan: " + facultyName + " - " + plan);
        }

        String year = periodService.getYear();
        String period = periodService.getPeriod();

        List<Historial> historials = historialService.getSessionsByCourses(studentId, courses);

        Map<Course, HistorialStatus> coursesMap = getCoursesMap(courses, historials);

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
    private int getApprovedCredits(Map<Course, HistorialStatus> coursesMap) {
        int approvedCredits = 0;
        for (Course course : coursesMap.keySet()) {
            if (HistorialStatus.FINISHED.equals(coursesMap.get(course))) {
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
    private Map<Course, HistorialStatus> getCoursesMap(List<Course> courses, List<Historial> historials) {
        Map<Course, HistorialStatus> coursesMap = new HashMap<>();
        for (Course course : courses) {
            historials.stream()
                    .filter(h -> h.getGroupCode().equals(course.getAbbreviation()))
                    .findFirst()
                    .ifPresentOrElse(
                            h -> coursesMap.put(course, h.getStatus()),
                            () -> coursesMap.put(course, HistorialStatus.PENDING));
        }
        return coursesMap;
    }

    /**
     * Calculates the percentage of completed courses for a student.
     *
     * @param studentId The unique identifier of the student
     * @return The percentage of completed courses
     */
    public double getCompletedCoursesPercentage(String studentId) {
        Student student = studentService.getStudentById(studentId);
        String facultyName = student.getFacultyName();
        String plan = student.getPlan();
        List<Course> facultyCourses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);
        if (facultyCourses.isEmpty()) {
            logger.error("Invalid faculty fullName or plan: " + facultyName + " - " + plan);
            throw new ResourceNotFoundException(
                    "Invalid faculty fullName or plan: " + facultyName + " - " + plan);
        }

        List<Historial> finishedHistorial = historialService.getHistorialByStudentIdAndStatus(studentId, HistorialStatus.FINISHED);

        int approvedCredits =
                finishedHistorial.stream()
                        .mapToInt(
                                historial -> {
                                    Group group = groupService.getGroupByGroupCode(historial.getGroupCode());
                                    return facultyCourses.stream()
                                            .filter(course -> course.getAbbreviation().equals(group.getAbbreviation()))
                                            .findFirst()
                                            .map(Course::getCredits)
                                            .orElse(0);
                                })
                        .sum();

        int totalCredits = facultyCourses.stream().mapToInt(Course::getCredits).sum();

        if (totalCredits == 0) {
            return 0.0;
        }
        return (double) approvedCredits / totalCredits * 100;
    }

    /**
     * Retrieves the status of all courses for a student.
     *
     * @param studentId The unique identifier of the student
     * @return A map of course abbreviations to their status
     */
    public Map<String, HistorialStatus> getStudentCoursesStatus(String studentId) {
        Student student = studentService.getStudentById(studentId);
        String facultyName = student.getFacultyName();
        String plan = student.getPlan();

        List<Course> facultyCourses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);

        if (facultyCourses.isEmpty()) {
            logger.error("Invalid faculty name or plan: " + facultyName + " - " + plan);
            throw new ResourceNotFoundException(
                    "Invalid faculty name or plan: " + facultyName + " - " + plan);
        }

        Map<String, HistorialStatus> courseStatusMap = new HashMap<>();

        List<Historial> studentHistorial = historialService.getHistorialByStudentId(studentId);

        for (Historial historial : studentHistorial) {
            String courseAbbreviation = historial.getGroupCode();
            HistorialStatus newStatus = historial.getStatus();
            HistorialStatus currentStatus = courseStatusMap.get(courseAbbreviation);

            if (currentStatus == null || currentStatus != HistorialStatus.FINISHED) {
                courseStatusMap.put(courseAbbreviation, newStatus);
            }
        }

        for (Course course : facultyCourses) {
            courseStatusMap.putIfAbsent(course.getAbbreviation(), HistorialStatus.PENDING);
        }

        return courseStatusMap;
    }
}