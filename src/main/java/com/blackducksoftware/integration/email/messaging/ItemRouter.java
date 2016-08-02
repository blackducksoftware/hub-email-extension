package com.blackducksoftware.integration.email.messaging;

public abstract class ItemRouter<D> implements Runnable {

	private RouterTaskData<D> taskData;

	public RouterTaskData<D> getTaskData() {
		return taskData;
	}

	public void setTaskData(final RouterTaskData<D> taskData) {
		this.taskData = taskData;
	}

	public abstract String getName();

	public abstract void execute(RouterTaskData<D> data);

	@Override
	public void run() {
		execute(getTaskData());
	}
}
