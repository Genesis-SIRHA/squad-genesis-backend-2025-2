package services;

import dto.ScheduleRequest;
import dto.ScheduleResponse;

import java.util.List;

public class SchedulerService {

    public ScheduleResponse createSchedule(ScheduleRequest scheduleRequest) {
        for (SessionDTO session : scheduleRequest.getSessions()) {

        }
    }

    public ScheduleResponse getScheduleById(String id) {
    }

    public List<ScheduleResponse> getAllSchedules() {
    }

    public ScheduleResponse updateSchedule(String id, ScheduleRequest scheduleRequest) {
    }

    public void deleteSchedule(String id) {
    }
}
