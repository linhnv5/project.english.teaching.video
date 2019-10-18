package topica.linhnv5.video.teaching.model;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Async task, hold information about a task 
 * @author ljnk975
 */
public class Task<T> {

	public enum Status {
		CREATED, RUNNING, FINISHED
	}

	/**
	 * ID of task
	 */
	private int id;

	/**
	 * The main task
	 */
	private Future<T> task;

	/**
	 * The percent progress estimate of task
	 */
	private byte progress;

	/**
	 * Status of task, begin with created
	 */
	private Status status = Status.CREATED;

	public Task() {
	}

	public Task(Future<T> task) {
		this.task = task;
		this.status = Status.RUNNING;
		this.progress = 0;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the progress
	 */
	public byte getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(byte progress) {
		if (progress <= 0)
			this.progress = 0;
		else if (progress >= 100) {
			this.progress = 100;
			this.status = Status.FINISHED;
		} else
			this.progress = progress;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the task
	 */
	public Future<T> getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Future<T> task) {
		this.task = task;
	}

	/**
	 * Blocking call, get the result
	 * @return the result of task
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public T get() throws InterruptedException, ExecutionException {
		return this.task.get();
	}

}
