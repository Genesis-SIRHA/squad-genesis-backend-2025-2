package edu.dosw.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Service class that handles academic period and year calculations.
 * Provides methods to determine the current academic period and year based on the system date.
 */
@Service
public class PeriodService {
    private static final int SUP_LIMIT_FIRST_PERIOD = 5;
    private static final int SUP_LIMIT_INTER_PERIOD = 7;

    private final Clock clock;

    @Autowired
    public PeriodService(Clock clock) {
        this.clock = clock;
    }

    /**
     * Determines the current academic period based on the system date.
     * The academic year is divided into three periods:
     * - '1' (First semester): January to May (inclusive)
     * - 'I' (Intersemester): June to July (inclusive)
     * - '2' (Second semester): August to December (inclusive)
     *
     * @return a String representing the current academic period ('1', 'I', or '2')
     */
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

    /**
     * Retrieves the current calendar year.
     *
     * @return the current year as a String
     */
    public String getYear() {
        return String.valueOf(LocalDateTime.now(clock).getYear());
    }
}

