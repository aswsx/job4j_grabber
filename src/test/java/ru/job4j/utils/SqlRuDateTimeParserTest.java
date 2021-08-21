package ru.job4j.utils;


import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlRuDateTimeParserTest {

    @Test
    public void whenDateAndTime() {
        LocalDateTime fullTimeAndDate = new SqlRuDateTimeParser().parse("12 янв 21, 18:18");
        LocalDateTime expected = LocalDateTime
                .of(LocalDate
                        .of(2021, 1, 12), LocalTime
                        .of(18, 18));
        Assert.assertEquals(expected, fullTimeAndDate);
    }

    @Test
    public void whenYesterday() {
        LocalDateTime yesterday = new SqlRuDateTimeParser().parse("вчера, 12:12");
        LocalDateTime exp = LocalDateTime
                .of(LocalDate.now().minusDays(1), LocalTime
                        .of(12, 12));
        Assert.assertEquals(exp, yesterday);
    }

    @Test
    public void whenToday() {
        LocalDateTime today = new SqlRuDateTimeParser().parse("сегодня, 15:15");
        LocalDateTime exp = LocalDateTime
                .of(LocalDate.now(), LocalTime
                        .of(15, 15));
        Assert.assertEquals(exp, today);
    }
}