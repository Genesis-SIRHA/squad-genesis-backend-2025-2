package edu.dosw.services;

import edu.dosw.dto.CourseRequest;
import edu.dosw.dto.GroupRequest;
import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.repositories.FacultyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
        ArrayList<Faculty> faculties = (ArrayList<Faculty>) facultyRepository.findAll();
        for (Faculty faculty : faculties) {
            facultyInfo.put(faculty.getFacultyName(), faculty.getPlan());
        }
        return facultyInfo;
    }

    public ArrayList<Course> findCoursesByFacultyNameAndPlan(String facultyName, String plan) {
        Optional<Faculty> faculty = facultyRepository.findByNameAndPlan(facultyName, plan);
        if (faculty.isEmpty()) {
            throw new BusinessException("Faculty not found: "+facultyName);
        }
        return faculty.get().getCourses();
    }

    /**
     * Creates a new course with the provided details.
     * @param request The course details to create
     * @return The created course
     * @throws BusinessException if a course with the same abbreviation already exists
     */
    public Faculty createCourse(CourseRequest request) {
        Optional<Faculty> faculty = facultyRepository.findByNameAndPlan(request.facultyName(), request.plan());

        if (faculty.isEmpty()) {
            throw new BusinessException("Faculty not found: "+request.facultyName());
        }

        if (faculty.get().getCourses().stream().anyMatch(c -> c.getAbbreviation().equals(request.abbreviation()))) {
            throw new BusinessException("Course already exists: "+request.abbreviation());
        }


        Course course = new Course();
        course.setAbbreviation(request.abbreviation());
        course.setCourseName(request.courseName());

        faculty.get().getCourses().add(course);
        Faculty facultyToSave = faculty.get();

        return facultyRepository.save(facultyToSave);
    }

    /**
     * Updates an existing course with new details.
     * @param id The ID of the course to update
     * @param request The updated course details
     * @return An Optional containing the updated Course if found, or empty if not found
     */
    public Faculty updateCourse(String id, CourseRequest request) {
        Optional<Faculty> faculty = facultyRepository.findByNameAndPlan(request.facultyName(), request.plan());

        if (faculty.isEmpty()) {
            throw new BusinessException("Faculty not found: "+request.facultyName());
        }

        if (faculty.get().getCourses().stream().anyMatch(c -> c.getAbbreviation().equals(request.abbreviation()))) {
            throw new BusinessException("Course already exists: "+request.abbreviation());
        }
        Course course = request.toEntity();
        faculty.get().getCourses().add(course);
        Faculty facultyToSave = faculty.get();
        return facultyRepository.save(facultyToSave);
    }

    /**
     * Adds a new group to an existing course.
     * @param groupRequest The details of the group to add
     * @return An Optional containing the updated Course if found, or empty if course not found
     * @throws BusinessException if a group with the same abbreviation already exists in the course
     */
    public Boolean addGroupToCourse(GroupRequest groupRequest) {
        Optional<Course> course = findCourseByCode(groupRequest.abbreviation());
        if (course.isEmpty()) {
            throw new BusinessException("Faculty not found: "+ groupRequest.abbreviation());
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
     * Finds a course by its abbreviation.
     * @param code The course abbreviation to search for
     * @return An Optional containing the Course if found, or empty if not found
     */
    public Optional<Course> findCourseByCode(String code) {
        ArrayList<Faculty> faculties = (ArrayList<Faculty>) facultyRepository.findAll();
        return faculties.stream()
                .map(faculty -> faculty.getCourses().stream()
                        .filter(course -> course.getAbbreviation().equals(code))
                        .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

}

