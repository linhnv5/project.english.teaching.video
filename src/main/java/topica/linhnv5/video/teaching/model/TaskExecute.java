package topica.linhnv5.video.teaching.model;

/**
 * Async task, hold information about a task 
 * @author ljnk975
 */
public class TaskExecute {

	/**
	 * The percent progress estimate of task
	 */
	private byte progress;

	/**
	 * Start time of task
	 */
	private long startMilis;

	/**
	 * Task
	 */
	private Task task;

	public TaskExecute() {
		this.task = new Task();
		this.progress = 0;
		this.startMilis = System.currentTimeMillis();
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
		else if (progress >= 100)
			this.progress = 100;
		else
			this.progress = progress;
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
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public boolean equals(Object obj) {
		return ((TaskExecute)obj).task.getId().equals(this.task.getId());
	}

	@Override
	public int hashCode() {
		return this.task.getId().hashCode();
	}

}
