package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Group;
import org.springframework.stereotype.Service;
import edu.dosw.repositories.CourseRepository;

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

    /**
     * Constructs a new CourseService with the provided CourseRepository.
     * @param courseRepository The repository for course data access
     */
    public CourseService(CourseRepository courseRepository, GroupService groupService) {
        this.courseRepository = courseRepository;
        this.groupService = groupService;
    }
    /**
     * Retrieves a list of all courses.
     * @return A list of CourseResponse objects representing all courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll().stream().collect(Collectors.toList());
    }
  
    /**
     * Retrieves a course by its abbreviation.
     * @param abbreviation The abbreation of the course to retrieve
     * @return An Optional containing the CourseResponse if found, or empty if not found
     */

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);


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

        Course savedCourse = courseRepository.save(course);
        return savedCourse;
    }

    /**
     * Updates an existing course with new details.
     * @param id The ID of the course to update
     * @param request The updated course details
     * @return An Optional containing the updated CourseResponse if found, or empty if not found
     */
    public Optional<Course> updateCourse(String id, CourseRequest request) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setCourseName(request.name());
                    Course updatedCourse = courseRepository.save(existingCourse);
                    return updatedCourse;
                });
    }
    /**
     * Adds a new group to an existing course.
     * @param abbreviation The ID of the course to add the group to
     * @param groupRequest The details of the group to add
     * @return An Optional containing the updated Course if found, or empty if course not found
     * @throws BusinessException if a group with the same code already exists in the course

     */
    public Optional<Course> addGroupToCourse(String courseId, GroupRequest groupRequest) {
        return courseRepository.findById(courseId)
                .map(course -> {
                    boolean groupExists = course.getGroups().stream()
                            .anyMatch(g -> g.getGroupCode().equals(groupRequest.groupCode()));
                    
                    if (groupExists) {
                        throw new BusinessException("Ya existe un grupo con el código: " + groupRequest.groupCode());
                    }

                    Group newGroup = mapToGroup(groupRequest);
                    course.getGroups().add(newGroup);
                    
                    Course updatedCourse = courseRepository.save(course);
                    return updatedCourse;
                });


    /**
     * Deletes a course by its abbreviation.
     * @param abbreviation The abbreviation of the course to delete
     */
    public void deleteCourse(String abbreviation) {
        courseRepository.deleteById(abbreviation);
    }

    /**
     * Maps a GroupRequest to a Group entity.
     * @param groupRequest The GroupRequest to map
     * @return A new Group entity with the request details
     */
    private Group mapToGroup(GroupRequest groupRequest) {
        Group group = new Group();
        group.setGroupCode(groupRequest.groupCode());
        group.setTeacherId(groupRequest.professor());
        group.setmaxCapacity(groupRequest.capacity());
        group.setEnrolled(groupRequest.enrolled());
        return group;
    }

    public Group findByCode(String s) {
        return courseRepository.findByCode(s);
    }
}