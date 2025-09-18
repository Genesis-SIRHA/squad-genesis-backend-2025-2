package services;


import model.Course;
import org.springframework.stereotype.Service;
import repositories.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(String id) {
        return courseRepository.findById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> updateCourse(String id, Course updatedCourse) {
        return courseRepository.findById(id).map(existing -> {
            existing.setCode(updatedCourse.getCode());
            existing.setName(updatedCourse.getName());
            existing.setGroups(updatedCourse.getGroups());
            return courseRepository.save(existing);
        });
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }
}