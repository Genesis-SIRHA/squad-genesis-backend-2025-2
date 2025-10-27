package edu.dosw.services.Validators;

import edu.dosw.dto.CoursesDto;
import edu.dosw.model.Course;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
        .forEach(
            entry -> {
              String semester = entry.getKey();
              List<Course> semesterCourses = entry.getValue();

              semesterCourses.forEach(
                  course -> {
                    if ("1".equals(semester)) {
                      if (!course.getRequisites().isEmpty()) {
                        logger.error("The first Semester classes cannot have requisites");
                        throw new IllegalArgumentException(
                            "The first Semester classes cannot have requisites");
                      }
                    } else {
                      List<String> invalidRequisites =
                          course.getRequisites().stream()
                              .filter(requisite -> !previousCourses.contains(requisite))
                              .collect(Collectors.toList());

                      if (!invalidRequisites.isEmpty()) {
                        logger.error(
                            "The course {} has requisites that are not in previous courses: {}",
                            course.getAbbreviation(),
                            invalidRequisites);
                        throw new IllegalArgumentException(
                            "The course "
                                + course.getAbbreviation()
                                + " has requisites that are not in previous courses: "
                                + invalidRequisites);
                      }
                    }
                    previousCourses.add(course.getAbbreviation());
                  });
            });
  }
}

