package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.CourseResponse;
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

    /**
     * Constructs a new CourseService with the provided CourseRepository.
     * @param courseRepository The repository for course data access
     */
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Retrieves a list of all courses.
     * @return A list of CourseResponse objects representing all courses
     */
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromModel)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a course by its ID.
     * @param id The ID of the course to retrieve
     * @return An Optional containing the CourseResponse if found, or empty if not found
     */
    public Optional<CourseResponse> getCourseById(String id) {
        return courseRepository.findById(id)
                .map(CourseResponse::fromModel);
    }

    /**
     * Creates a new course with the provided details.
     * @param request The course details to create
     * @return The created course as a CourseResponse
     * @throws BusinessException if a course with the same code already exists
     */
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCode(request.code())) {
            throw new BusinessException("Ya existe un curso con el código: " + request.code());
        }

        Course course = new Course();
        course.setCode(request.code());
        course.setName(request.name());
        
        if (request.groups() != null) {
            List<Group> groups = request.groups().stream()
                    .map(this::mapToGroup)
                    .collect(Collectors.toList());
            course.setGroups(groups);
        }

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.fromModel(savedCourse);
    }

    /**
     * Updates an existing course with new details.
     * @param id The ID of the course to update
     * @param request The updated course details
     * @return An Optional containing the updated CourseResponse if found, or empty if not found
     */
    public Optional<CourseResponse> updateCourse(String id, CourseRequest request) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setName(request.name());
                    
                    if (request.groups() != null) {
                        List<Group> updatedGroups = request.groups().stream()
                                .map(this::mapToGroup)
                                .collect(Collectors.toList());
                        existingCourse.setGroups(updatedGroups);
                    }
                    
                    Course updatedCourse = courseRepository.save(existingCourse);
                    return CourseResponse.fromModel(updatedCourse);
                });
    }

    /**
     * Adds a new group to an existing course.
     * @param courseId The ID of the course to add the group to
     * @param groupRequest The details of the group to add
     * @return An Optional containing the updated CourseResponse if found, or empty if course not found
     * @throws BusinessException if a group with the same code already exists in the course
     */
    public Optional<CourseResponse> addGroupToCourse(String courseId, GroupRequest groupRequest) {
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
                    return CourseResponse.fromModel(updatedCourse);
                });
    }

    /**
     * Deletes a course by its ID.
     * @param id The ID of the course to delete
     */
    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    /**
     * Maps a GroupRequest to a Group entity.
     * @param groupRequest The GroupRequest to map
     * @return A new Group entity with the request details
     */
    private Group mapToGroup(GroupRequest groupRequest) {
        Group group = new Group();
        group.setGroupCode(groupRequest.groupCode());
        group.setProfessor(groupRequest.professor());
        group.setCapacity(groupRequest.capacity());
        group.setEnrolled(groupRequest.enrolled());
        return group;
    }
}