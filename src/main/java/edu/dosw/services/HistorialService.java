package edu.dosw.services;

import edu.dosw.dto.HistorialDTO;
import edu.dosw.exception.BusinessException;
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

  public List<String> getGroupCodesByStudentIdAndPeriod(
      String studentId, String year, String period) {
    ArrayList<Historial> historial =
        historialRepository.findHistorialByStudentIdAndYearAndPeriod(studentId, year, period);
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

  public List<Historial> getSessionsByCourses(String studentId, List<Course> courses) {
    List<Historial> completeHistorial = historialRepository.findByStudentId(studentId);
    List<Historial> lastCourseState = new ArrayList<>();

    for (Course course : courses) {
      List<Historial> historialByCourse =
          completeHistorial.stream()
              .filter(h -> h.getGroupCode().equals(course.getAbbreviation()))
              .toList();

      if (!historialByCourse.isEmpty()) {
        lastCourseState.add(historialByCourse.get(historialByCourse.size() - 1));
      }
    }
    return lastCourseState;
  }

  /**
   * Adds historial of a groyp for a student
   *
   * @param dto
   * @return
   */
  public Historial addHistorial(HistorialDTO dto) {
    historialValidator.validateHistorialCreation(dto);

    Historial existing =
        historialRepository.findByStudentIdAndGroupCode(dto.studentId(), dto.groupCode());

    if (existing != null) {
      throw new BusinessException(
          "Ya existe un historial para el estudiante "
              + dto.studentId()
              + " y el grupo "
              + dto.groupCode());
    }

    Historial historial =
        new Historial.HistorialBuilder()
            .studentId(dto.studentId())
            .groupCode(dto.groupCode())
            .status(dto.status())
            .year(periodService.getYear())
            .period(periodService.getPeriod())
            .build();

    try {
      return historialRepository.save(historial);
    } catch (Exception e) {
      throw new BusinessException("Error al guardar el historial", e);
    }
  }

  /**
   * Updates the historial of a student for a certain groyp
   *
   * @param studentId
   * @param groupCode
   * @param newStatus
   * @return
   */
  public Historial updateHistorial(String studentId, String groupCode, HistorialStatus newStatus) {
    try {
      Historial historial = getByStudentIdAndGroupCode(studentId, groupCode);

      historialValidator.historialUpdateValidator(historial.getStatus(), newStatus);

      historial.setStatus(newStatus);
      return historialRepository.save(historial);

    } catch (Exception e) {
      throw new BusinessException(
          "Error al actualizar el historial del estudiante "
              + studentId
              + " en el grupo "
              + groupCode,
          e);
    }
  }

  public List<Historial> getAllHistorial() {
    return historialRepository.findAll();
  }

  /**
   * Retrieves all historical records for a student
   *
   * @param studentId The unique identifier of the student
   * @return List of all historical records
   */
  public List<Historial> getHistorialByStudentId(String studentId) {
    try {
      List<Historial> historials = historialRepository.findByStudentId(studentId);

      if (historials == null || historials.isEmpty()) {
        throw new BusinessException(
            "No se encontraron historiales para el estudiante " + studentId);
      }

      return historials;
    } catch (Exception e) {
      throw new BusinessException(
          "Error al obtener los historiales del estudiante " + studentId, e);
    }
  }

  public List<Historial> getHistorialByStudentIdAndStatus(
      String studentId, HistorialStatus status) {
    return historialRepository.findByStudentIdAndStatus(studentId, status);
  }

  /**
   * Retrieves all historical records for a student
   *
   * @param studentId The unique identifier of the student
   * @return List of all historical records
   */
  public List<Historial> getAllHistorialsByStudentId(String studentId) {
    try {
      List<Historial> historials = historialRepository.findByStudentId(studentId);

      if (historials == null || historials.isEmpty()) {
        throw new BusinessException(
            "No se encontraron historiales para el estudiante " + studentId);
      }

      return historials;
    } catch (Exception e) {
      throw new BusinessException(
          "Error al obtener los historiales del estudiante " + studentId, e);
    }
  }
}
