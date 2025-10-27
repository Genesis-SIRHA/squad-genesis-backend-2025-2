package edu.dosw.services.Validators;

import edu.dosw.dto.CoursesDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.dosw.model.Course;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class FacultyValidator {
    private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);

    public void validateAddCourses(CoursesDto coursesDto) {
        List<String> previousCourses = new ArrayList<>();

        coursesDto.courses().entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> Integer.parseInt(entry.getKey())))
                .forEach(entry -> {
                    String semester = entry.getKey();
                    List<Course> semesterCourses = entry.getValue();

                    semesterCourses.forEach(course -> {
                        if ("1".equals(semester)) {
                            if (!course.getPrerequisites().isEmpty()) {
                                logger.error("The first Semester classes cannot have prerequisites");
                                throw new IllegalArgumentException("The first Semester classes cannot have prerequisites");
                            }
                        } else {
                            List<String> invalidPrerequisites = course.getPrerequisites().stream()
                                    .filter(prerequisite -> !previousCourses.contains(prerequisite))
                                    .collect(Collectors.toList());

                            if (!invalidPrerequisites.isEmpty()) {
                                logger.error("The course {} has prerequisites that are not in previous courses: {}",
                                        course.getAbbreviation(), invalidPrerequisites);
                                throw new IllegalArgumentException("The course " + course.getAbbreviation() +
                                        " has prerequisites that are not in previous courses: " +
                                        invalidPrerequisites);
                            }
                        }
                        previousCourses.add(course.getAbbreviation());
                    });
                });
    }
}

