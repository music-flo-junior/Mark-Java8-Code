package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class DateTime {

    @Test
    public void duration() {
        Instant start = Instant.now();
        runAlgorithm();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        long millis = timeElapsed.toMillis();

        Instant start2 = Instant.now();
        runAlgorithm2();
        Instant end2 = Instant.now();
        Duration timeElapsed2 = Duration.between(start2, end2);

        boolean overTenTimesFaster = timeElapsed.multipliedBy(10).minus(timeElapsed2).isNegative();
        assertFalse(overTenTimesFaster);
    }

    private void runAlgorithm() {
        for (int i = 0; i < 10000; i++) ;
    }


    private void runAlgorithm2() {
        for (int i = 0; i < 1000; i++) ;
    }

    @Test
    public void local_time() {
        LocalDate today = LocalDate.now();
        LocalDate alonzosBirthday = LocalDate.of(1903, 6, 14);
        alonzosBirthday = LocalDate.of(1903, Month.JUNE, 14);

        LocalDate programmersDay = LocalDate.of(2014, 1, 1).plusDays(255);

        LocalDate.of(1900, 1, 1).getDayOfWeek().getValue();
        assertEquals(DayOfWeek.SATURDAY.plus(3), DayOfWeek.TUESDAY);
    }

    @Test
    public void day_adjuster() {
        LocalDate firstTuseday = LocalDate.of(2020, 8, 1).with(
                TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY)
        );
    }

    @Test
    public void zone_date_time() {
        ZoneId.getAvailableZoneIds();

        ZonedDateTime apollo11launch = ZonedDateTime.of(1969, 7, 16, 9, 32, 0, 0, ZoneId.of("America/New_York"));
        System.out.println(apollo11launch);

        ZonedDateTime skipped = ZonedDateTime.of(
                LocalDate.of(2013, 3, 31),
                LocalTime.of(2, 30),
                ZoneId.of("Europe/Berlin")
        );

        ZonedDateTime ambiguous = ZonedDateTime.of(
                LocalDate.of(2013, 10, 27), // 일광 절약 시간 끝
                LocalTime.of(2, 30),
                ZoneId.of("Europe/Berlin")
        );
        ZonedDateTime anHourLater = ambiguous.plusHours(1);

        // zone date 를 사용하면 날짜 조정시 Duration이 아닌 Period 를 사용해야 한다.
        ZonedDateTime nextMeetingWrong = ZonedDateTime.now().plus(Duration.ofDays(7));
        ZonedDateTime nextMeeting = ZonedDateTime.now().plus(Period.ofDays(7));
    }

    @Test
    public void formatter() {
        LocalDateTime now = LocalDateTime.now();

        System.out.println(DateTimeFormatter.BASIC_ISO_DATE.format(now));

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        System.out.println(formatter.format(now));
        System.out.println(formatter.withLocale(Locale.FRANCE).format(now));

        formatter = DateTimeFormatter.ofPattern("E yyyy-MM-dd HH:mm");

        // 표준 ISO_LOCAL_DATE
        LocalDate churchsBirthday = LocalDate.parse("1903-06-14");
        // 커스텀 포맷터
        ZonedDateTime apollo11launch =
                ZonedDateTime.parse("1969-07-16 03:32:00-0400", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssxx"));
    }
}

