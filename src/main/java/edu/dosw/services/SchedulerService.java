package edu.dosw.services;

import edu.dosw.model.Historial;
import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
        historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);
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
  public Schedule getScheduleById(String studentId) {
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
    return buildSchedule(studentId, year, period);
  }

  /**
   * Retrieves all past schedules of the student
   *
   * @param studentId The unique identifier of the student
   * @return List of past schedules
   */
  public List<Schedule> getPastSchedules(String studentId) {
    String currentYear = periodService.getYear();
    String currentPeriod = periodService.getPeriod();

    List<Historial> allHistorials = historialService.getAllHistorialsByStudentId(studentId);

    Set<String[]> uniquePeriods = new LinkedHashSet<>();

    for (Historial historial : allHistorials) {
      uniquePeriods.add(new String[] {historial.getYear(), historial.getPeriod()});
    }

    List<String[]> pastPeriods =
        uniquePeriods.stream()
            .filter(
                periodInfo -> {
                  String year = periodInfo[0];
                  String period = periodInfo[1];
                  return year.compareTo(currentYear) < 0
                      || (year.equals(currentYear) && period.compareTo(currentPeriod) < 0);
                })
            .sorted(
                (a, b) -> {
                  int yearCompare = b[0].compareTo(a[0]);
                  return yearCompare != 0 ? yearCompare : b[1].compareTo(a[1]);
                })
            .collect(Collectors.toList());

    List<Schedule> pastSchedules = new ArrayList<>();
    for (String[] periodInfo : pastPeriods) {
      Schedule schedule = buildSchedule(studentId, periodInfo[0], periodInfo[1]);
      pastSchedules.add(schedule);
    }

    return pastSchedules;
  }
}
