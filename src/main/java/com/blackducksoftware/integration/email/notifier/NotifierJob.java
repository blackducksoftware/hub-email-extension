package com.blackducksoftware.integration.email.notifier;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotifierJob implements Job {

	public static final String JOB_DATA_KEY_NOTIFIER = "notifier-object-key";

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		final JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		if (dataMap.containsKey(JOB_DATA_KEY_NOTIFIER)) {
			final AbstractNotifier notifier = (AbstractNotifier) dataMap.get(JOB_DATA_KEY_NOTIFIER);
			notifier.run();
		}
	}
}
