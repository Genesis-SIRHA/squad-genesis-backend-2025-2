package edu.dosw.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class PeriodServiceTest {

    private PeriodService createServiceWithFixedDate(int year, int month, int day) {
        LocalDateTime fixedDate = LocalDateTime.of(year, month, day, 10, 0);
        Clock fixedClock = Clock.fixed(
                fixedDate.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
        return new PeriodService(fixedClock);
    }

    @ParameterizedTest
    @CsvSource({
            "2024, 1, 15, 1",
            "2024, 2, 28, 1",
            "2024, 3, 15, 1",
            "2024, 4, 30, 1",
            "2024, 5, 31, 1"
    })
    void getPeriod_ShouldReturnFirstPeriod_WhenMonthIsBetweenJanuaryAndMay(
            int year, int month, int day, String expectedPeriod) {
        PeriodService service = createServiceWithFixedDate(year, month, day);

        String result = service.getPeriod();

        assertEquals(expectedPeriod, result);
    }

    @ParameterizedTest
    @CsvSource({
            "2024, 6, 1, I",
            "2024, 6, 15, I",
            "2024, 7, 1, I",
            "2024, 7, 31, I"
    })
    void getPeriod_ShouldReturnIntersemester_WhenMonthIsJuneOrJuly(
            int year, int month, int day, String expectedPeriod) {
        PeriodService service = createServiceWithFixedDate(year, month, day);

        String result = service.getPeriod();

        assertEquals(expectedPeriod, result);
    }

    @ParameterizedTest
    @CsvSource({
            "2024, 8, 1, 2",
            "2024, 9, 15, 2",
            "2024, 10, 31, 2",
            "2024, 11, 15, 2",
            "2024, 12, 31, 2"
    })
    void getPeriod_ShouldReturnSecondPeriod_WhenMonthIsBetweenAugustAndDecember(
            int year, int month, int day, String expectedPeriod) {
        PeriodService service = createServiceWithFixedDate(year, month, day);

        String result = service.getPeriod();

        assertEquals(expectedPeriod, result);
    }

    @Test
    void getPeriod_ShouldReturnFirstPeriod_OnJanuary1st() {
        PeriodService service = createServiceWithFixedDate(2024, 1, 1);

        String result = service.getPeriod();

        assertEquals("1", result);
    }

    @Test
    void getPeriod_ShouldReturnFirstPeriod_OnMay31st() {
        PeriodService service = createServiceWithFixedDate(2024, 5, 31);

        String result = service.getPeriod();

        assertEquals("1", result);
    }

    @Test
    void getPeriod_ShouldReturnIntersemester_OnJune1st() {
        PeriodService service = createServiceWithFixedDate(2024, 6, 1);

        String result = service.getPeriod();

        assertEquals("I", result);
    }

    @Test
    void getPeriod_ShouldReturnIntersemester_OnJuly31st() {
        PeriodService service = createServiceWithFixedDate(2024, 7, 31);

        String result = service.getPeriod();

        assertEquals("I", result);
    }

    @Test
    void getPeriod_ShouldReturnSecondPeriod_OnAugust1st() {
        PeriodService service = createServiceWithFixedDate(2024, 8, 1);

        String result = service.getPeriod();

        assertEquals("2", result);
    }

    @Test
    void getPeriod_ShouldReturnSecondPeriod_OnDecember31st() {
        PeriodService service = createServiceWithFixedDate(2024, 12, 31);

        String result = service.getPeriod();

        assertEquals("2", result);
    }

    @Test
    void getYear_ShouldReturnCurrentYear() {
        PeriodService service = createServiceWithFixedDate(2024, 6, 15);

        String result = service.getYear();

        assertEquals("2024", result);
    }

    @Test
    void getYear_ShouldReturnCorrectYear_ForDifferentYears() {
        PeriodService service2023 = createServiceWithFixedDate(2023, 1, 1);
        PeriodService service2025 = createServiceWithFixedDate(2025, 12, 31);

        assertEquals("2023", service2023.getYear());
        assertEquals("2025", service2025.getYear());
    }

    @Test
    void getYear_ShouldReturnStringRepresentation() {
        PeriodService service = createServiceWithFixedDate(2024, 6, 15);

        String result = service.getYear();

        assertInstanceOf(String.class, result);
    }

    @Test
    void getPeriod_ShouldWorkConsistently_AcrossDifferentYears() {
        PeriodService service2023 = createServiceWithFixedDate(2023, 3, 15);
        PeriodService service2024 = createServiceWithFixedDate(2024, 3, 15);
        PeriodService service2025 = createServiceWithFixedDate(2025, 3, 15);

        assertEquals("1", service2023.getPeriod());
        assertEquals("1", service2024.getPeriod());
        assertEquals("1", service2025.getPeriod());
    }

    @Test
    void getPeriodAndYear_ShouldWorkTogether() {
        PeriodService service = createServiceWithFixedDate(2024, 6, 15);

        String period = service.getPeriod();
        String year = service.getYear();

        assertEquals("I", period);
        assertEquals("2024", year);
    }
}