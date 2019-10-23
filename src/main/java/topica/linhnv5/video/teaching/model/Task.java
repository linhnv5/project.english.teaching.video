package topica.linhnv5.video.teaching.model;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Async task, hold information about a task 
 * @author ljnk975
 */
public class Task<T> {

	/**
	 * Staus of task T
	 * @author ljnk975
	 */
	public enum Status {
		CREATED, RUNNING, FINISHED
	}

	/**
	 * ID of task
	 */
	private String id;

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

	/**
	 * Start time of task
	 */
	private long startMilis;

	/**
	 * End time of task
	 */
	private long endMilis;

	public Task() {
		this.status = Status.CREATED;
		this.progress = 0;
		this.startMilis = System.currentTimeMillis();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
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
			this.endMilis = System.currentTimeMillis();
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
	 * @return the startMilis
	 */
	public long getStartMilis() {
		return startMilis;
	}

	/**
	 * @param startMilis the startMilis to set
	 */
	public void setStartMilis(long startMilis) {
		this.startMilis = startMilis;
	}

	/**
	 * @return the endMilis
	 */
	public long getEndMilis() {
		return endMilis;
	}

	/**
	 * @param endMilis the endMilis to set
	 */
	public void setEndMilis(long endMilis) {
		this.endMilis = endMilis;
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
