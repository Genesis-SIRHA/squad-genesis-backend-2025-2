package edu.dosw.services;

import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing and building student schedules.
 * Coordinates between SessionService, HistorialService, and PeriodService to
 * construct comprehensive schedule information for students.
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
    public SchedulerService(SessionService sessionService,
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
        ArrayList<String> groupCodes = historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);
        ArrayList<Session> sessions = new ArrayList<>();

        for (String groupCode : groupCodes) {
            sessions.addAll(sessionService.getSessionsByGroupCode(groupCode));
        }

        return new Schedule(studentId, sessions);
    }

    /**
     * Retrieves the current schedule for a student.
     * Automatically determines the current academic year and period.
     *
     * @param studentId The unique identifier of the student
     * @return A Schedule object containing the student's current sessions
     */
    public Schedule getScheduleById(String studentId) {
        String year = periodService.getYear();
        String period = periodService.getPeriod();
        return buildSchedule(studentId, year, period);
    }
}
