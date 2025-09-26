package edu.dosw.services;

import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private final SessionService sessionService;
    private final HistorialService historialService;
    private final PeriodService periodService;

    @Autowired
    public SchedulerService(SessionService sessionService,
                            HistorialService historialService,
                            PeriodService periodService) {
        this.sessionService = sessionService;
        this.historialService = historialService;
        this.periodService = periodService;
    }

    private Schedule buildSchedule(String studentId, String year, String period) {
        ArrayList<String> groupCodes = historialService.getCurrentSessionsByStudentIdAndPeriod(studentId, year, period);
        ArrayList<Session> sessions = new ArrayList<>();

        for (String groupCode : groupCodes) {
            sessions.addAll(sessionService.getSessionsByGroupCode(groupCode));
        }

        return new Schedule(studentId, sessions);
    }

    public Schedule getScheduleById(String studentId) {
        String year = periodService.getYear();
        String period = periodService.getPeriod();
        return buildSchedule(studentId, year, period);
    }
}
