package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotifierManager {
    private final Logger logger = LoggerFactory.getLogger(NotifierManager.class);

    private final Map<String, AbstractNotifier> notifierMap = new ConcurrentHashMap<>();

    private Scheduler scheduler;

    public NotifierManager() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (final SchedulerException ex) {
            logger.error("Error instantiating scheduler", ex);
        }
    }

    public void updateHubExtensionUri(final String hubExtensionUri) {
        for (final Map.Entry<String, AbstractNotifier> entry : notifierMap.entrySet()) {
            entry.getValue().setHubExtensionUri(hubExtensionUri);
        }
    }

    public void attach(final AbstractNotifier notifier) {
        final List<AbstractNotifier> notifierList = new Vector<>();
        notifierList.add(notifier);
        attach(notifierList);
    }

    public void attach(final List<AbstractNotifier> notifiers) {
        for (final AbstractNotifier notifier : notifiers) {
            final String notifierKey = notifier.getNotifierPropertyKey();
            notifierMap.put(notifierKey, notifier);
        }
    }

    public void unattach(final AbstractNotifier notifier) {
        final List<AbstractNotifier> notifierList = new ArrayList<>();
        notifierList.add(notifier);
        unattach(notifierList);
    }

    public void unattach(final List<AbstractNotifier> notifierList) {
        for (final AbstractNotifier notifier : notifierList) {
            final String notifierKey = notifier.getNotifierPropertyKey();
            if (notifierMap.containsKey(notifierKey)) {
                notifierMap.remove(notifierKey);
            }
        }
    }

    public void unattachAll() {
        notifierMap.entrySet().forEach(e -> {
            unattach(e.getValue());
        });
    }

    public void start() {
        if (scheduler == null) {
            logger.error("scheduler is null; cannot start notifiers");
        } else {

            try {
                for (final Map.Entry<String, AbstractNotifier> entry : notifierMap.entrySet()) {
                    start(entry.getValue());
                }
                scheduler.start();
            } catch (final SchedulerException e) {
                logger.error("Exception occurred starting scheduler", e);
            }
        }
    }

    public void start(final AbstractNotifier notifier) {
        // if no interval is defined then don't start the notifier
        if (StringUtils.isNotBlank(notifier.getCronExpression())) {
            try {
                final JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put(NotifierJob.JOB_DATA_KEY_NOTIFIER, notifier);
                final JobDetail jobDetail = JobBuilder.newJob(NotifierJob.class).setJobData(jobDataMap)
                        .withIdentity("Job-" + notifier.getName()).build();
                final CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(notifier.getCronExpression())
                        .inTimeZone(TimeZone.getTimeZone("UTC"));
                final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Trigger-" + notifier.getName())
                        .withSchedule(cronSchedule).forJob(jobDetail).build();
                scheduler.scheduleJob(jobDetail, trigger);
            } catch (final SchedulerException e) {
                logger.error("Error scheduling notifier to start {}", notifier.getName(), e);
            }
        }
    }

    public void stop() {
        try {
            if (scheduler == null) {
                logger.error("scheduler is null; cannot shutdown notifiers");
            } else {
                scheduler.shutdown();
            }
        } catch (final SchedulerException e) {
            logger.error("Exception occurred stopping the scheduler", e);
        }
    }

    public List<AbstractNotifier> getNotifiers() {
        final List<AbstractNotifier> list = new ArrayList<>();
        notifierMap.entrySet().forEach(e -> {
            e.getValue();
        });
        return list;
    }
}
