package edu.dosw.services;

import edu.dosw.model.Group;
import edu.dosw.model.Schedule;
import edu.dosw.model.Session;
import edu.dosw.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class SchedulerService {
    private final SessionService sessionService;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public SchedulerService(SessionService sessionService, ScheduleRepository scheduleRepository) {
        this.sessionService = sessionService;
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule getScheduleById(String studentId) {
        return scheduleRepository.findById(studentId).orElse(null);
    }

    public void updateSchedule(String studentId, String abbreviation, String groupId) {
        Schedule schedule = this.getScheduleById(studentId);
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule not found");
        }
        schedule.deleteGroup(abbreviation, groupId);
    }

    public void updateSchedule(String studentId, Group group) {
        Schedule schedule = this.getScheduleById(studentId);
        if (schedule == null) {
            throw  new IllegalArgumentException("Schedule not found");
        }
        ArrayList<Session> sessions = sessionService.getSessionsByAbbreviationAndGroup(group.getAbbreviation(), group.getGroupCode());
        schedule.addSessions(sessions);

    }

    public void updateSchedule(String studentId, String abbreviation, String originGroup, String newGroup) {
        Schedule schedule = this.getScheduleById(studentId);
        if (schedule == null) {
            throw new IllegalArgumentException("Schedule not found");
        }
        schedule.deleteGroup(abbreviation, originGroup);
        schedule.addSession(sessionService.getSessionsByAbbreviationAndGroup(abbreviation, newGroup));
    }

    public void deleteSchedule(String studentId) {
        scheduleRepository.deleteByStudentId(studentId);
    }
}
