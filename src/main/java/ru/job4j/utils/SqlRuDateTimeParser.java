package ru.job4j.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Класс преобразует дату в системный формат
 */
public class SqlRuDateTimeParser implements DateTimeParser {
    /**
     * Мапа хранит ключи- названия месяцев и соответствующую им нумерацию
     */
    private static final Map<String, String> MONTHS = Map.ofEntries(
            entry("янв", "01"),
            entry("фев", "02"),
            entry("мар", "03"),
            entry("апр", "04"),
            entry("май", "05"),
            entry("июн", "06"),
            entry("июл", "07"),
            entry("авг", "08"),
            entry("сен", "09"),
            entry("окт", "10"),
            entry("ноя", "11"),
            entry("дек", "12")
    );

    /**
     * Метод преобразует дату и время в системный формат
     *
     * @param parse строка, содержащая дату и время
     * @return возврат даты/времени в системном формате
     * dateFormatter шаблон даты
     * timeFormatter шаблон времени
     * dateSplit запись в массив отдельно даты и времени, разделитель ","
     * date запись в массив даты по частям, разделитель " "
     * localDate определение текущей даты
     * localTime форматирование времени
     */
    @Override
    public LocalDateTime parse(String parse) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d M yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] dateSplit = parse.split(",");
        String[] date = dateSplit[0].split(" ");
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.parse(dateSplit[1], timeFormatter);

        if (date.length == 3) { // если дата задана в виде "12 янв 2007"
            LocalDate.parse(String.format("%s %s %s", date[0], MONTHS.get(date[1]), date[2]),
                    dateFormatter);
        } else if (date[0].startsWith("сегодня")) { // если дата в виде "сегодня"
            LocalDate.now();
        } else if (date[0].startsWith("вчера")) { // если дата в виде "вчера"
            LocalDate.now().minusDays(1);
        } else {
            throw new IllegalArgumentException("Illegal date");
        }
        return LocalDateTime.of(localDate, localTime);
    }
}
