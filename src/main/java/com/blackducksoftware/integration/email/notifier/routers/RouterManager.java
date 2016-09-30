package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

public class RouterManager {
	private final Logger logger = LoggerFactory.getLogger(RouterManager.class);
	private final Map<String, AbstractRouter> routerMap = new ConcurrentHashMap<>();
	private Scheduler scheduler;

	public RouterManager() {
		Scheduler tmpScheduler = null;
		try {
			tmpScheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (final SchedulerException ex) {
			logger.error("Error instantiating scheduler", ex);
		} finally {
			scheduler = tmpScheduler;
		}
	}

	public void updateHubExtensionId(final String hubExtensionId) {
		for (final Map.Entry<String, AbstractRouter> entry : routerMap.entrySet()) {
			entry.getValue().setHubExtensionId(hubExtensionId);
		}
	}

	public void attachRouter(final AbstractRouter router) {
		final List<AbstractRouter> routerList = new Vector<>();
		routerList.add(router);
		attachRouters(routerList);
	}

	public void attachRouters(final List<AbstractRouter> routers) {
		for (final AbstractRouter router : routers) {
			final String routerKey = router.getRouterPropertyKey();
			routerMap.put(routerKey, router);
		}
	}

	public void unattachRouter(final AbstractRouter router) {
		final List<AbstractRouter> routerList = new ArrayList<>();
		routerList.add(router);
		unattachRouters(routerList);
	}

	public void unattachRouters(final List<AbstractRouter> routerList) {
		for (final AbstractRouter router : routerList) {
			final String routerKey = router.getRouterPropertyKey();
			if (routerMap.containsKey(routerKey)) {
				routerMap.remove(routerKey);
			}
		}
	}

	public void unattachAllRouters() {
		routerMap.entrySet().forEach(e -> {
			unattachRouter(e.getValue());
		});
	}

	public void startRouters() {
		if (scheduler == null) {
			logger.error("scheduler is null; cannot start routers");
		} else {

			try {
				for (final Map.Entry<String, AbstractRouter> entry : routerMap.entrySet()) {
					startRouter(entry.getValue());
				}
				scheduler.start();
			} catch (final SchedulerException e) {
				logger.error("Exception occurred starting scheduler", e);
			}
		}
	}

	public void startRouter(final AbstractRouter router) {
		// if no interval is defined then don't start the router
		if (StringUtils.isNotBlank(router.getCronExpression())) {
			try {
				final JobDataMap jobDataMap = new JobDataMap();
				jobDataMap.put(RouterJob.JOB_DATA_KEY_ROUTER, router);
				final JobDetail jobDetail = JobBuilder.newJob(RouterJob.class).setJobData(jobDataMap)
						.withIdentity("Job-" + router.getName()).build();
				final CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(router.getCronExpression());
				final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("Trigger-" + router.getName())
						.withSchedule(cronSchedule).forJob(jobDetail).build();
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (final SchedulerException e) {
				logger.error("Error scheduling router to start {}", router.getName(), e);
			}
		}
	}

	public void stopRouters() {
		try {
			if (scheduler == null) {
				logger.error("scheduler is null; cannot shutdown routers");
			} else {
				scheduler.shutdown();
			}
		} catch (final SchedulerException e) {
			logger.error("Exception occurred stoping the scheduler", e);
		}
	}

	public List<AbstractRouter> getRouters() {
		final List<AbstractRouter> list = new ArrayList<>();
		routerMap.entrySet().forEach(e -> {
			e.getValue();
		});
		return list;
	}
}
