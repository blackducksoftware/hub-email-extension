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
		final Set<String> routerKeyList = routerMap.keySet();
		for (final String routerKey : routerKeyList) {
			final AbstractRouter router = routerMap.get(routerKey);
			stopRouter(router);
			startRouter(router);
		}
	}

	public void startRouter(final AbstractRouter router) {
		final String routerKey = router.getRouterPropertyKey();
		if (timerMap.containsKey(routerKey)) {
			stopRouter(router);
			timerMap.remove(routerKey);
		}
		final Timer timer = new Timer("RouterTimer-" + routerKey);
		timerMap.put(routerKey, timer);
		timer.scheduleAtFixedRate(router, router.getStartDelayMilliseconds(), router.getIntervalMilliseconds());
	}

	public void stopRouters() {
		final Set<String> routerKeyList = routerMap.keySet();
		for (final String routerKey : routerKeyList) {
			if (timerMap.containsKey(routerKey)) {
				final Timer timer = timerMap.get(routerKey);
				timer.cancel();
				timerMap.remove(routerKey);
			}
		}
	}

	public void stopRouter(final AbstractRouter router) {
		final String routerKey = router.getRouterPropertyKey();
		if (timerMap.containsKey(routerKey)) {
			final Timer timer = timerMap.get(routerKey);
			timer.cancel();
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
