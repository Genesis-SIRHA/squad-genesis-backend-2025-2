package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Course;
import edu.dosw.model.Historial;
import edu.dosw.model.Pemsum;
import edu.dosw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class PemsumService {
    private final FacultyService facultyService;
    private final MembersService membersService;
    private final HistorialService historialService;

    @Autowired
    public PemsumService(FacultyService facultyService, MembersService membersService, HistorialService historialService) {
        this.facultyService = facultyService;
        this.membersService = membersService;
        this.historialService = historialService;
    }

    public Pemsum getPemsum(String studentId) {
        return buildPemsum(studentId);
    }

    private Pemsum buildPemsum(String studentId) {
        Pemsum pemsum = new Pemsum();
        User student = membersService.listById(studentId);
        String facultyName = student.getFacultyName();
        String plan = student.getPlan();

        pemsum.setStudentName(student.getName());
        pemsum.setStudentId(studentId);
        pemsum.setFacultyName(facultyName);
        pemsum.setFacultyPlan(plan);

        ArrayList<Course> courses = facultyService.findCoursesByFacultyNameAndPlan(facultyName, plan);
        String year = PeriodService.getYear();
        String period = PeriodService.getPeriod();
        ArrayList<Historial> historials = historialService.getSessionsByStudentIdAndPeriod(studentId, year, period);

        if (courses.isEmpty()) {
            throw new BusinessException("Invalid faculty name or plan: "+facultyName+" - "+plan);
        }

        int totalCredits = courses.stream().mapToInt(Course::getCredits).sum();
        Map<Course, String> coursesMap = getCoursesMap(courses, historials);
        int approvedCredits = getApprovedCredits(coursesMap);

        pemsum.setTotalCredits(totalCredits);
        pemsum.setCourses(coursesMap);
        pemsum.setApprovedCredits(approvedCredits);

        return pemsum ;
    }

    private int getApprovedCredits(Map<Course, String> coursesMap) {
        int approvedCredits = 0;
        for (Course course : coursesMap.keySet()) {
            if (coursesMap.get(course).equals("approved")) {
                approvedCredits += course.getCredits();
            }
        }
        return approvedCredits;
    }

    private Map<Course, String> getCoursesMap(ArrayList<Course> courses, ArrayList<Historial> historials) {
        Map<Course, String> coursesMap = new HashMap<>();
        for (Course course : courses) {
            if (historials.stream().anyMatch(h -> h.getGroupCode().equals(course.getAbbreviation()))) {
                coursesMap.put(course, historials.stream().filter(h -> h.getGroupCode().equals(course.getAbbreviation())).findFirst().get().getStatus());
                continue;
            }
            coursesMap.put(course, "pending");
        }
        return coursesMap;
    }

}
