package com.blackducksoftware.integration.email.notifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.email.notifier.routers.AbstractEmailRouter;
import com.blackducksoftware.integration.email.notifier.routers.EmailContentItem;
import com.blackducksoftware.integration.email.notifier.routers.EmailTaskData;
import com.blackducksoftware.integration.email.notifier.routers.factory.AbstractEmailFactory;

public abstract class AbstractPollingDispatcher extends TimerTask {
	private final Logger logger = LoggerFactory.getLogger(AbstractPollingDispatcher.class);

	public static long DEFAULT_POLLING_INTERVAL = 10000;
	public static long DEFAULT_POLLING_DELAY = 5000;

	private Timer timer;
	private long interval;
	private long startupDelay;
	private final Map<String, AbstractEmailFactory> templateFactoryMap = new ConcurrentHashMap<>();
	private final Map<String, Set<String>> typeToTemplateMap = new ConcurrentHashMap<>();
	private ExecutorService executorService;

	private Date lastRun;
	private Date currentRun;
	private String name;

	public AbstractPollingDispatcher() {
		interval = DEFAULT_POLLING_INTERVAL;
		startupDelay = DEFAULT_POLLING_DELAY;
	}

	public void start() {
		String startMsg = "Started";
		if (StringUtils.isNotBlank(getName())) {
			startMsg += ": " + getName();
		} else {
			startMsg += " polling for messages";
		}
		logger.info(startMsg);
		stopTimer();
		timer = new Timer();
		timer.schedule(this, startupDelay, interval);
	}

	public void stop() {
		stopTimer();
		shutdown();
		String stopMsg = "Stopped";
		if (StringUtils.isNotBlank(getName())) {
			stopMsg += ": " + getName();
		} else {
			stopMsg += " polling for messages";
		}
		logger.info(stopMsg);
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void attachRouter(final AbstractEmailFactory router) {
		final List<AbstractEmailFactory> routerList = new Vector<>();
		routerList.add(router);
		attachRouters(routerList);
	}

	public void attachRouters(final List<AbstractEmailFactory> routers) {
		for (final AbstractEmailFactory factory : routers) {
			final String templateName = factory.getTemplateName();
			templateFactoryMap.put(templateName, factory);
			final Set<String> typeNameSet = factory.getTemplateContentTypes();
			for (final String typeName : typeNameSet) {
				Set<String> templateNameSet;
				if (typeToTemplateMap.containsKey(typeName)) {
					templateNameSet = typeToTemplateMap.get(typeName);
				} else {
					templateNameSet = new HashSet<>();
					typeToTemplateMap.put(typeName, templateNameSet);
				}
				templateNameSet.add(factory.getTemplateName());
			}
		}
	}

	public void unattachRouter(final AbstractEmailFactory router) {
		final List<AbstractEmailFactory> factoryList = new ArrayList<>();
		factoryList.add(router);
		unattachRouters(factoryList);
	}

	public void unattachRouters(final List<AbstractEmailFactory> routerFactories) {
		for (final AbstractEmailFactory factory : routerFactories) {
			final String templateName = factory.getTemplateName();
			if (templateFactoryMap.containsKey(templateName)) {
				final Set<String> typeNameSet = factory.getTemplateContentTypes();
				// remove the template type mappings
				for (final String typeName : typeNameSet) {
					if (typeToTemplateMap.containsKey(typeName)) {
						final Set<String> templateSet = typeToTemplateMap.get(typeName);
						templateSet.remove(factory.getTemplateName());
					}
				}
				templateFactoryMap.remove(templateName);
				if (logger.isDebugEnabled()) {
					logger.debug("Router removed: " + factory);
					logger.debug("Current router count: " + templateFactoryMap.size());
				}
			}
		}
	}

	private void shutdown() {
		logger.info("Shutting down event dispatching thread pool.");
		// cleanup topics router map
		if (executorService != null) {
			executorService.shutdown();
		}
		templateFactoryMap.clear(); // remove all topics
		typeToTemplateMap.clear();
	}

	@Override
	public void run() {
		currentRun = new Date();
		final List<EmailContentItem> itemList = fetchData();
		final Map<String, List<Object>> partitionedData = partitionData(itemList);
		final Map<String, EmailTaskData> dataMap = filterData(partitionedData);
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Execution data: " + System.lineSeparator() + "########## Polling Dispatcher Execution ##########"
							+ System.lineSeparator() + "Dispatcher Name  = " + name + System.lineSeparator()
							+ "Polling interval = " + interval + System.lineSeparator() + "Last Run         = "
							+ lastRun + System.lineSeparator() + "Current Run      = " + currentRun
							+ System.lineSeparator() + "##################################################");
		}
		final Set<String> factoryTopics = dataMap.keySet();
		for (final String topic : factoryTopics) {
			if (templateFactoryMap.containsKey(topic)) {
				final AbstractEmailFactory factory = templateFactoryMap.get(topic);
				final AbstractEmailRouter<?> router = factory.createInstance(dataMap.get(topic));
				executorService.submit(router);
			}
		}
		lastRun = currentRun;
	}

	public Date getCurrentRun() {
		return currentRun;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(final long interval) {
		this.interval = interval;
	}

	public long getStartupDelay() {
		return startupDelay;
	}

	public void setStartupDelay(final long startupDelay) {
		this.startupDelay = startupDelay;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(final ExecutorService executorService) {
		this.executorService = executorService;
	}

	public abstract void init();

	public abstract List<EmailContentItem> fetchData();

	public Map<String, List<Object>> partitionData(final List<EmailContentItem> dataList) {
		final Map<String, List<Object>> partitionMap = new HashMap<>();
		for (final Object item : dataList) {
			final String classname = item.getClass().getName();
			if (typeToTemplateMap.containsKey(classname)) {
				final Set<String> templateSet = typeToTemplateMap.get(classname);
				for (final String templateName : templateSet) {
					List<Object> partitionList;
					if (partitionMap.containsKey(templateName)) {
						partitionList = partitionMap.get(templateName);
					} else {
						partitionList = new Vector<>();
						partitionMap.put(templateName, partitionList);
					}
					partitionList.add(item);
				}
			}
		}
		return partitionMap;
	}

	public abstract Map<String, EmailTaskData> filterData(Map<String, List<Object>> partitionedData);
}
