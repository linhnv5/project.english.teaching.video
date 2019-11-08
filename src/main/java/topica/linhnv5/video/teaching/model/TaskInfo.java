package topica.linhnv5.video.teaching.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * Result for api get task status
 * @author ljnk975
 */
public class TaskInfo {

	/**
	 * Status of task
	 */
	@ApiModelProperty("Status of task, RUNNING, FINISHED, CREATED")
	private String status;

	/**
	 * Error occur
	 */
	@ApiModelProperty("Contain infomation about error occur")
	private String error;

	/**
	 * Percent progress
	 */
	@ApiModelProperty("Progress percent")
	private int progress;

	/**
	 * Time eslapse
	 */
	@ApiModelProperty("Consuming time of this task")
	private long timeConsume;

	public TaskInfo() {
	}
	
	public TaskInfo(String status, String error, int progress) {
		this.status = status;
		this.error = error;
		this.progress = progress;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	/**
	 * @return the timeConsume
	 */
	public long getTimeConsume() {
		return timeConsume;
	}

	/**
	 * @param timeConsume the timeConsume to set
	 */
	public void setTimeConsume(long timeConsume) {
		this.timeConsume = timeConsume;
	}

}
