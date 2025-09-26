package edu.dosw.services;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class PeriodServiceTest {

    private PeriodService buildServiceWithDate(int year, int month, int day) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, 0, 0);
        Clock fixedClock = Clock.fixed(dateTime.atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        return new PeriodService(fixedClock);
    }

    @Test
    void getPeriod_shouldReturn1_whenMonthIsWithinFirstPeriod() {
        PeriodService service = buildServiceWithDate(2025, 3, 10);
        String result = service.getPeriod();
        assertThat(result).isEqualTo("1");
    }

    @Test
    void getPeriod_shouldReturnI_whenMonthIsWithinInterPeriod() {
        PeriodService service = buildServiceWithDate(2025, 6, 10);
        String result = service.getPeriod();
        assertThat(result).isEqualTo("I");
    }

    @Test
    void getPeriod_shouldReturn2_whenMonthIsAfterInterPeriod() {
        PeriodService service = buildServiceWithDate(2025, 9, 10);
        String result = service.getPeriod();
        assertThat(result).isEqualTo("2");
    }

    @Test
    void getYear_shouldReturnCorrectYear() {
        PeriodService service = buildServiceWithDate(2030, 1, 1);
        String result = service.getYear();
        assertThat(result).isEqualTo("2030");
    }
}
