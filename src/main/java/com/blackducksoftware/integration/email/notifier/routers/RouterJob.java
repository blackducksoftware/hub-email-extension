package com.blackducksoftware.integration.email.notifier.routers;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RouterJob implements Job {

	public static final String JOB_DATA_KEY_ROUTER = "router-object-key";

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		final JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		if (dataMap.containsKey(JOB_DATA_KEY_ROUTER)) {
			final AbstractRouter router = (AbstractRouter) dataMap.get(JOB_DATA_KEY_ROUTER);
			router.run();
		}
	}
}
