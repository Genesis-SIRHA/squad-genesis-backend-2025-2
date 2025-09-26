package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import edu.dosw.repositories.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class that handles business logic related to courses and groups.
 * Provides methods for CRUD operations on courses and their associated groups.
 */
@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final GroupService groupService;

    public CourseService(CourseRepository courseRepository, GroupService groupService) {
        this.courseRepository = courseRepository;
        this.groupService = groupService;
    }

    /**
     * Retrieves a list of all courses.
     * @return A list of courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream().collect(Collectors.toList());
    }

    /**
     * Retrieves a course by its ID.
     * @param id The id of the course to retrieve
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    /**
     * Creates a new course with the provided details.
     * @param request The course details to create
     * @return The created course
     * @throws BusinessException if a course with the same code already exists
     */
    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCode(request.code())) {
            throw new BusinessException("Already exists a course with code: " + request.code());
        }

        Course course = new Course();
        course.setAbbreviation(request.code());
        course.setCourseName(request.name());

        return courseRepository.save(course);
    }

    /**
     * Updates an existing course with new details.
     * @param id The ID of the course to update
     * @param request The updated course details
     * @return An Optional containing the updated Course if found, or empty if not found
     */
    public Optional<Course> updateCourse(String id, CourseRequest request) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setCourseName(request.name());
                    return courseRepository.save(existingCourse);
                });
    }

    /**
     * Adds a new group to an existing course.
     * @param abbreviation The ID of the course to add the group to
     * @param groupRequest The details of the group to add
     * @return An Optional containing the updated Course if found, or empty if course not found
     * @throws BusinessException if a group with the same code already exists in the course
     */
    public Boolean addGroupToCourse(String abbreviation, GroupRequest groupRequest) {
         Optional<Course> course = courseRepository.findById(abbreviation);
         if (course.isEmpty()) {
             throw new BusinessException("Course not found with id: " + abbreviation);
         }
         Group group = groupService.createGroup(groupRequest);
         return group != null;
    }

    /**
     * Deletes a course by its code.
     * @param code The code of the course to delete
     */
    public void deleteCourse(String code) {
        courseRepository.deleteById(code);
    }

    /**
     * Maps a GroupRequest to a Group entity.
     * @param groupRequest The GroupRequest to map
     * @return A new Group entity with the request details
     */
    private Group mapToGroup(GroupRequest groupRequest) {
        Group group = new Group();
        group.setGroupCode(groupRequest.groupCode());
        group.setTeacherId(groupRequest.teacherId());
        group.setMaxCapacity(groupRequest.maxCapacity());
        group.setEnrolled(groupRequest.enrolled());
        return group;
    }

    /**
     * Finds a course by its code.
     * @param code The course code to search for
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }
}

