package ru.job4j.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    public static void main(String[] args) {
        try {
            var scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            var job = newJob(Rabbit.class).build();
            var times = simpleSchedule()
                    .withIntervalInSeconds(getInterval())
                    .repeatForever();
            var trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    private static int getInterval() {
        var interval = 0;
        var config = new Properties();
        try {
            var in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties");
            config.load(in);
            interval = Integer.parseInt(config.getProperty("rabbit.interval"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interval;
    }


    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("The Truth Is Out There..." + getInterval());
        }
    }
}