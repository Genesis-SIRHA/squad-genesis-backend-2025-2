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

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromModel)
                .collect(Collectors.toList());
    }

    public Optional<CourseResponse> getCourseById(String id) {
        return courseRepository.findById(id)
                .map(CourseResponse::fromModel);
    }

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

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    private Group mapToGroup(GroupRequest groupRequest) {
        Group group = new Group();
        group.setGroupCode(groupRequest.groupCode());
        group.setProfessor(groupRequest.professor());
        group.setCapacity(groupRequest.capacity());
        group.setEnrolled(groupRequest.enrolled());
        return group;
    }
}