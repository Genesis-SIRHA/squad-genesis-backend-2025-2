package edu.dosw.services;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.exception.ResourceNotFoundException;
import edu.dosw.model.Course;
import edu.dosw.model.Historial;
import edu.dosw.model.enums.HistorialStatus;
import edu.dosw.repositories.HistorialRepository;
import edu.dosw.services.Validators.HistorialValidator;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HistorialService {
  private final HistorialValidator historialValidator;
  private final HistorialRepository historialRepository;
  private final PeriodService periodService;
  private final Logger logger = LoggerFactory.getLogger(HistorialService.class);

  public List<String> getCurrentSessionsByStudentIdAndPeriod(
      String studentId, String year, String period) {
    ArrayList<Historial> historial =
        historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(studentId, year, period);
    ArrayList<String> groupCodes = new ArrayList<>();
    for (Historial h : historial) {
      groupCodes.add(h.getGroupCode());
    }
    return groupCodes;
  }

  public Historial getByStudentIdAndGroupCode(String studentId, String groupCode) {
    Historial historial = historialRepository.findByStudentIdAndGroupCode(studentId, groupCode);
    if (historial == null) {
      logger.error("historial does not exist");
      throw new ResourceNotFoundException(
          "historial not found with studentId " + studentId + " and groupCode " + groupCode);
    }
    return historial;
  }

  public List<Historial> getSessionsByStudentIdYearAndPeriod(
      String studentId, String year, String period) {
    return historialRepository.findCurrentSessionsByStudentIdAndYearAndPeriod(
        studentId, year, period);
  }

  public List<Historial> getSessionsByCourses(String studentId, List<Course> courses) {
    List<Historial> completeHistorial = historialRepository.findByStudentId(studentId);
    ArrayList<Historial> lastCourseState = new ArrayList<>();
    for (Course course : courses) {
      ArrayList<Historial> historialByCourse =
          (ArrayList<Historial>)
              completeHistorial.stream()
                  .filter(h -> h.getGroupCode().equals(course.getAbbreviation()))
                  .toList();
      lastCourseState.add(historialByCourse.get(historialByCourse.size() - 1));
    }
    return lastCourseState;
  }

  public Historial addHistorial(HistorialDTO historialDTO) {
    getByStudentIdAndGroupCode(historialDTO.studentId(), historialDTO.groupCode());
    historialValidator.validateHistorialCreation(historialDTO);

    Historial historial =
        new Historial.HistorialBuilder()
            .studentId(historialDTO.studentId())
            .groupCode(historialDTO.groupCode())
            .status(historialDTO.status())
            .year(periodService.getYear())
            .period(periodService.getPeriod())
            .build();
    historialRepository.save(historial);
    return historial;
  }

  public Historial updateHistorial(String studentId, String groupCode, HistorialStatus newStatus) {
    Historial historial = getByStudentIdAndGroupCode(studentId, groupCode);
    historialValidator.historialUpdateValidator(historial.getStatus(), newStatus);

    historial.setStatus(newStatus);
    historialRepository.save(historial);
    return historial;
  }

  public List<Historial> getAllHistorial() {
    return historialRepository.findAll();
  }
}
