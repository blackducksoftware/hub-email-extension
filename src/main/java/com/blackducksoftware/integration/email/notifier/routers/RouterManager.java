package com.blackducksoftware.integration.email.notifier.routers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class RouterManager {

	private final Map<String, AbstractRouter> routerMap = new ConcurrentHashMap<>();
	private final Map<String, Timer> timerMap = new ConcurrentHashMap<>();

	public void attachRouter(final AbstractRouter router) {
		final List<AbstractRouter> routerList = new Vector<>();
		routerList.add(router);
		attachRouters(routerList);
	}

	public void attachRouters(final List<AbstractRouter> routers) {
		for (final AbstractRouter router : routers) {
			final String templateName = router.getTemplateName();
			routerMap.put(templateName, router);
		}
	}

	public void unattachRouter(final AbstractRouter router) {
		final List<AbstractRouter> routerList = new ArrayList<>();
		routerList.add(router);
		unattachRouters(routerList);
	}

	public void unattachRouters(final List<AbstractRouter> routerList) {
		for (final AbstractRouter router : routerList) {
			final String templateName = router.getTemplateName();
			if (routerMap.containsKey(templateName)) {
				routerMap.remove(templateName);
			}
		}
	}

	public void startRouters() {
		final Set<String> templateNameList = routerMap.keySet();
		for (final String templateName : templateNameList) {
			final AbstractRouter router = routerMap.get(templateName);
			stopRouter(router);
			startRouter(router);
		}
	}

	public void startRouter(final AbstractRouter router) {
		final String templateName = router.getTemplateName();
		final Timer timer = new Timer("RouterTimer-" + templateName);
		timerMap.put(templateName, timer);
		timer.scheduleAtFixedRate(router, router.getStartDelayMilliseconds(), router.getIntervalMilliseconds());
	}

	public void stopRouters() {
		final Set<String> templateNameList = routerMap.keySet();
		for (final String templateName : templateNameList) {
			if (timerMap.containsKey(templateName)) {
				final Timer timer = timerMap.get(templateName);
				timer.cancel();
				timerMap.remove(templateName);
			}
		}
	}

	public void stopRouter(final AbstractRouter router) {
		final String templateName = router.getTemplateName();
		if (timerMap.containsKey(templateName)) {
			final Timer timer = timerMap.get(templateName);
			timer.cancel();
		}
	}
}
