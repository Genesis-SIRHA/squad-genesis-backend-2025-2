package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.repositories.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class that handles business logic related to courses and groups.
 * Provides methods for CRUD operations on courses and their associated groups.
 */
@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final GroupService groupService;

    public FacultyService(FacultyRepository facultyRepository, GroupService groupService) {
        this.facultyRepository = facultyRepository;
        this.groupService = groupService;
    }

    public Map<String, String> getAllFacultyName() {
        Map<String, String> facultyInfo = new HashMap<>();
        List<Faculty> faculties = facultyRepository.findAll(); // ✅ sin cast
        for (Faculty faculty : faculties) {
            facultyInfo.put(faculty.getFacultyName(), faculty.getPlan());
        }
        return facultyInfo;
    }

    public List<Course> findCoursesByFacultyNameAndPlan(String facultyName, String plan) {
        Optional<Faculty> faculty = facultyRepository.findByNameAndPlan(facultyName, plan);
        if (faculty.isEmpty()) {
            throw new BusinessException("Faculty not found: " + facultyName);
        }
        return new ArrayList<>(faculty.get().getCourses());
    }

    /**
     * Creates a new course with the provided details.
     * @param request The course details to create
     * @return The created course
     * @throws BusinessException if a course with the same abbreviation already exists
     */
    public Faculty createCourse(CourseRequest request) {
        Faculty faculty = facultyRepository.findByNameAndPlan(request.facultyName(), request.plan())
                .orElseThrow(() -> new BusinessException("Faculty not found: " + request.facultyName()));

        if (faculty.getCourses().stream().anyMatch(c -> c.getAbbreviation().equals(request.abbreviation()))) {
            throw new BusinessException("Course already exists: " + request.abbreviation());
        }

        Course course = request.toEntity();
        faculty.getCourses().add(course);

        return facultyRepository.save(faculty);
    }

    /**
     * Updates an existing course with new details.
     * @param id The ID of the course to update
     * @param request The updated course details
     * @return An Optional containing the updated Course if found, or empty if not found
     */
    public Faculty updateCourse(String id, CourseRequest request) {
        Faculty faculty = facultyRepository.findByNameAndPlan(request.facultyName(), request.plan())
                .orElseThrow(() -> new BusinessException("Faculty not found: " + request.facultyName()));

        if (faculty.getCourses().stream().anyMatch(c -> c.getAbbreviation().equals(request.abbreviation()))) {
            throw new BusinessException("Course already exists: " + request.abbreviation());
        }

        Course course = request.toEntity();
        faculty.getCourses().add(course);

        return facultyRepository.save(faculty);
    }

    /**
     * Adds a new group to an existing course.
     * @param groupRequest The details of the group to add
     * @return true if the group was added successfully
     * @throws BusinessException if course is not found
     */
    public Boolean addGroupToCourse(GroupRequest groupRequest) {
        Optional<Course> course = findCourseByCode(groupRequest.abbreviation());
        if (course.isEmpty()) {
            throw new BusinessException("Faculty not found: " + groupRequest.abbreviation());
        }
        Group group = groupService.createGroup(groupRequest);
        return group != null;
    }

    /**
     * Deletes a course by its abbreviation.
     * @param code The abbreviation of the course to delete
     */
    public void deleteCourse(String code) {
        facultyRepository.deleteById(code);
    }

    /**
     * Finds a course by its abbreviation.
     * @param code The course abbreviation to search for
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> findCourseByCode(String code) {
        List<Faculty> faculties = facultyRepository.findAll();
        return faculties.stream()
                .flatMap(faculty -> faculty.getCourses().stream())
                .filter(course -> course.getAbbreviation().equals(code))
                .findFirst();
    }
}
