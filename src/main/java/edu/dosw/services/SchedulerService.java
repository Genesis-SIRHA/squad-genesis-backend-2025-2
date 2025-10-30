package edu.dosw.services;

import edu.dosw.exception.BusinessException;
import edu.dosw.model.Historial;
import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing and building student schedules. Coordinates between
 * SessionService, HistorialService, and PeriodService to construct comprehensive schedule
 * information for students.
 */
@Service
public class SchedulerService {
  private final SessionService sessionService;
  private final HistorialService historialService;
  private final PeriodService periodService;

  /**
   * Constructs a new SchedulerService with the required services.
   *
   * @param sessionService Service for managing session data
   * @param historialService Service for accessing student historical records
   * @param periodService Service for handling academic period information
   */
  @Autowired
  public SchedulerService(
      SessionService sessionService,
      HistorialService historialService,
      PeriodService periodService) {
    this.sessionService = sessionService;
    this.historialService = historialService;
    this.periodService = periodService;
  }

  /**
   * Builds a schedule for a student based on their enrolled groups for a specific academic period.
   *
   * @param studentId The unique identifier of the student
   * @param year The academic year
   * @param period The academic period (e.g., '1', 'I', '2')
   * @return A Schedule object containing all the student's sessions for the period
   */
  private Schedule buildSchedule(String studentId, String year, String period) {
    List<String> groupCodes =
        historialService.getGroupCodesByStudentIdAndPeriod(studentId, year, period);
    ArrayList<Session> sessions = new ArrayList<>();

    for (String groupCode : groupCodes) {
      sessions.addAll(sessionService.getSessionsByGroupCode(groupCode));
    }

    return new Schedule(studentId, sessions);
  }

  /**
   * Retrieves the current schedule for a student. Automatically determines the current academic
   * year and period.
   *
   * @param studentId The unique identifier of the student
   * @return A Schedule object containing the student's current sessions
   */
  public Schedule getActualScheduleByStudentId(String studentId) {
    String year = periodService.getYear();
    String period = periodService.getPeriod();
    return buildSchedule(studentId, year, period);
  }

  /**
   * Retrieves the schedule of a specified period
   *
   * @param studentId The unique identifier of the student
   * @param year The academic year
   * @param period The academic period
   * @return A schedule object containing the student's sessions of the specified period
   */
  public Schedule getScheduleByPeriod(String studentId, String year, String period) {
    try {
      return buildSchedule(studentId, year, period);
    } catch (Exception e) {
      throw new BusinessException(
          "Error al obtener el horario del estudiante "
              + studentId
              + " para el periodo "
              + year
              + " - "
              + period,
          e);
    }
  }

  /**
   * Retrieves all past schedules of the student
   *
   * @param studentId The unique identifier of the student
   * @return List of past schedules
   */
  public List<Schedule> getPastSchedules(String studentId) {
    try {
      String currentYear = periodService.getYear();
      String currentPeriod = periodService.getPeriod();

      List<Historial> historials = historialService.getAllHistorialsByStudentId(studentId);

      Collection<Historial> uniqueHistorials =
          historials.stream()
              .collect(
                  Collectors.toMap(
                      h -> h.getYear() + "-" + h.getPeriod(),
                      h -> h,
                      (h1, h2) -> h1,
                      LinkedHashMap::new))
              .values();

      return uniqueHistorials.stream()
          .filter(h -> isBefore(h.getYear(), h.getPeriod(), currentYear, currentPeriod))
          .sorted(
              Comparator.comparing(Historial::getYear)
                  .reversed()
                  .thenComparing(Historial::getPeriod, Comparator.reverseOrder()))
          .map(h -> buildSchedule(studentId, h.getYear(), h.getPeriod()))
          .collect(Collectors.toList());

    } catch (BusinessException e) {
      throw e;
    } catch (Exception e) {
      throw new BusinessException(
          "Error al obtener los horarios pasados del estudiante " + studentId, e);
    }
  }

  private boolean isBefore(String year, String period, String currentYear, String currentPeriod) {
    return year.compareTo(currentYear) < 0
        || (year.equals(currentYear) && period.compareTo(currentPeriod) < 0);
  }
}
