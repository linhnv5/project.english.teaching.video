package topica.linhnv5.video.teaching.controller.response;

import io.swagger.annotations.ApiModelProperty;

/**
 * Response for subbing video service, include task id, error code, ...
 * @author ljnk975
 */
public class TaskCreate {

	/**
	 * Status of request, SUCCESS if command return OK, ERROR if there are some error occur
	 */
	@ApiModelProperty("Status of request, SUCCESS for OK, ERROR othewise")
	private String status;

	/**
	 * Request error message
	 */
	@ApiModelProperty("If the status is ERROR, this return infomation about error occur")
	private String mess;

	/**
	 * Task id of request, using to check status of request and get the result
	 */
	@ApiModelProperty("The task id")
	private String task;

	/**
	 * Deafult constructor
	 */
	public TaskCreate() {
	}

	/**
	 * Main constructor
	 * @param status
	 * @param code
	 * @param errMss
	 * @param task
	 */
	public TaskCreate(String status, String errMss, String task) {
		this.status = status;
		this.mess = errMss;
		this.task = task;
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
	 * @return the errMess
	 */
	public String getMess() {
		return mess;
	}

	/**
	 * @param mess the errMess to set
	 */
	public void setMess(String mess) {
		this.mess = mess;
	}

	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(String task) {
		this.task = task;
	}

	
}
