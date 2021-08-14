package ru.job4j.quartz;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static ru.exxo.jutil.Printer.*;

public class AlertRabbit {
    private static final Logger LOG = LoggerFactory.getLogger(AlertRabbit.class.getName());

    public static void main(String[] args) throws Exception {
        var properties = new Properties();

        try (var stream =
                     AlertRabbit.class
                             .getClassLoader()
                             .getResourceAsStream("rabbit.properties")
        ) {
            properties.load(stream);
        }
        var interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
        Class.forName(properties.getProperty("jdbc.driver"));
        try (var connection = DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        )) {
            var scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            var data = new JobDataMap();
            data.put("connection", connection);
            var job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            var times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            var trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
            println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            println("Rabbit runs here ...");
            var connection = (Connection) context.getJobDetail()
                    .getJobDataMap()
                    .get("connection");
            try (var statement = connection.createStatement()) {
                statement.execute(
                        "INSERT INTO rabbit (created_date) VALUES (current_timestamp)"
                );
            } catch (SQLException e) {
                LOG.error("Rabbit is died...", e);
            }
        }
    }
}
