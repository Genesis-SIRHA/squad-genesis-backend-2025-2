package edu.dosw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class PeriodService {
    private static final int SUP_LIMIT_FIRST_PERIOD = 5;
    private static final int SUP_LIMIT_INTER_PERIOD = 7;

    private final Clock clock;

    @Autowired
    public PeriodService(Clock clock) {
        this.clock = clock;
    }

    public String getPeriod() {
        int month = LocalDateTime.now(clock).getMonthValue();
        if (month <= SUP_LIMIT_FIRST_PERIOD) {
            return "1";
        } else if (month <= SUP_LIMIT_INTER_PERIOD) {
            return "I";
        } else {
            return "2";
        }
    }

    public String getYear() {
        return String.valueOf(LocalDateTime.now(clock).getYear());
    }
}

