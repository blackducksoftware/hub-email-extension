package com.blackducksoftware.integration.email.messaging;

public abstract class ItemRouter<D> implements Runnable {

	private final RouterTaskData<D> taskData;

	public ItemRouter(final RouterTaskData<D> taskData) {
		this.taskData = taskData;
	}

	public RouterTaskData<D> getTaskData() {
		return taskData;
	}

	public abstract String getName();

	public abstract void execute(RouterTaskData<D> data);

	@Override
	public void run() {
		execute(getTaskData());
	}
}
