package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.html.Post;
import ru.job4j.html.SqlRuParse;
import ru.job4j.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();

    /**
     * Метод создает объект класса PsqlStore, передает в него cfg для инициализации
     *
     * @return возвращает готовый объект
     */
    public Store store() throws SQLException {
        return new PsqlStore(cfg);
    }

    /**
     * Метод создает шедулер
     *
     * @return возвращаемый шедулер
     * @throws SchedulerException исключение при ошибке создания
     */
    public Scheduler scheduler() throws SchedulerException {
        var scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * метод считывает файл properties
     *
     * @throws IOException исключение ввода/ввывода
     */
    public void cfg() throws IOException {
        try (var in = Grabber.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {
            cfg.load(in);
        }
    }

    /**
     * Метод инициализирует переменные и создает работу по расписанию
     *
     * @param parse     входной объект Parse
     * @param store     входной объект Store
     * @param scheduler входной шедулер
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) {
        var data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        var job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        var times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("time")))
                .repeatForever();
        var trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        shedulerShutdown(scheduler, job, trigger);
    }

    private void shedulerShutdown(Scheduler scheduler, JobDetail job, SimpleTrigger trigger) {
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000); //добавил выключатель, иначе программа выполняется бесконечно
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public static class GrabJob implements Job {
        /**
         * Метод выполняет созданную работу
         *
         * @param context контекст работы
         */
        @Override
        public void execute(JobExecutionContext context) {
            var map = context.getJobDetail().getJobDataMap();
            var store = (Store) map.get("store");
            var parse = (Parse) map.get("parse");
            try {
                for (Post post : parse.list("https://www.sql.ru/forum/job-offers")) {
                    store.save(post);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        var sqlDTParser = new SqlRuDateTimeParser();
        var grab = new Grabber();
        grab.cfg();
        var scheduler = grab.scheduler();
        var store = grab.store();
        grab.init(new SqlRuParse(sqlDTParser), store, scheduler);
    }
}