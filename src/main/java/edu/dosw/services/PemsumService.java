package edu.dosw.services;

import edu.dosw.model.Course;
import edu.dosw.model.Faculty;
import edu.dosw.model.Pemsum;
import edu.dosw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
public class PemsumService {
    private final FacultyService facultyService;
    private final MembersService membersService;
    private final CourseService courseService;

    @Autowired
    public PemsumService(FacultyService facultyService, MembersService membersService) {
        this.facultyService = facultyService;
        this.membersService = membersService;
    }

    public Pemsum getPemsum() {
        return buildPemsum();
    }

    private Pemsum buildPemsum(String studentId) {
        User student = membersService.listById(studentId);
        String studentName = student.getName();
        String facultyName = student.getFacultyName();
        String plan = student.getPlan();

        ArrayList<Course> courses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);
        int totalCredits = courses.stream().mapToInt(Course::getCredits).sum();


        return new Pemsum(studentName, facultyName, plan, totalCredits) ;
    }

}
