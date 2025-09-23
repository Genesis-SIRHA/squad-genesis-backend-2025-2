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

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final GroupService groupService;

    public CourseService(CourseRepository courseRepository, GroupService groupService) {
        this.courseRepository = courseRepository;
        this.groupService = groupService;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseByAbbreviation(String abbreviation) {
        return courseRepository.findById(abbreviation);
    }

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

    public Optional<Course> updateCourse(String id, CourseRequest request) {
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setCourseName(request.name());
                    Course updatedCourse = courseRepository.save(existingCourse);
                    return updatedCourse;
                });
    }

    public Optional<Course> addGroupToCourse(String abbreviation, GroupRequest groupRequest) {
        List<Group> groups = groupService.getAllGroupsByCourseAbbreviation(abbreviation);
        if (groups.stream().anyMatch(g -> g.getGroupCode().equals(groupRequest.groupCode()))) {
            throw new BusinessException("Group already exists");
        }

        groupService.createGroup(groupRequest);
        return this.getCourseByAbbreviation(abbreviation);
    }

    public void deleteCourse(String abbreviation) {
        courseRepository.deleteById(abbreviation);
    }

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