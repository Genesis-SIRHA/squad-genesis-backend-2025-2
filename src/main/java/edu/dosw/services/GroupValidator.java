package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Faculty;
import edu.dosw.model.Group;
import edu.dosw.model.Student;
import edu.dosw.services.UserServices.StudentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GroupValidator {
    private final PeriodService periodService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final Logger logger = LoggerFactory.getLogger(GroupValidator.class);

    public void validateAddStudentToGroup(Group group, String studentId) {
        if (group.getEnrolled() == group.getMaxCapacity()) {
            logger.error("The group {} is full", group.getGroupCode());
            throw new BusinessException("The group is full");
        }
        if (!group.getYear().equals(periodService.getYear())
                || !group.getPeriod().equals(periodService.getPeriod())) {
            logger.error(
                    "The historial period and year does not match the one from the group: {} != {}",
                    group.getPeriod(),
                    periodService.getPeriod());
            throw new BusinessException(
                    "The historial period and year does not match the one from the group"
                            + group.getPeriod()
                            + " != "
                            + periodService.getPeriod());
        }

        Student student = studentService.getStudentById(studentId);
        Faculty faculty =
                facultyService.getFacultyByNameAndPlan(student.getFacultyName(), student.getPlan());
        if (faculty.getCourses().stream()
                .filter(c -> c.getAbbreviation().equals(group.getAbbreviation()))
                .findFirst()
                .get()
                .getAbbreviation()
                == null) {
            logger.error("The destination group is not in your plan");
            throw new BusinessException("The destination group is not in your plan");
        }
    }
}