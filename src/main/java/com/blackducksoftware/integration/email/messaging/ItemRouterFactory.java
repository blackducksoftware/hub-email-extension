package com.blackducksoftware.integration.email.messaging;

public abstract class ItemRouterFactory<D> implements RouterFactorySubscriber {

	public abstract ItemRouter<D> createInstance(RouterTaskData<D> data);
}
