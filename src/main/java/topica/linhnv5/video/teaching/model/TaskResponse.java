package topica.linhnv5.video.teaching.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * Response for subbing video service, include task id, error code, ...
 * @author ljnk975
 */
public class TaskResponse {

	/**
	 * Header of request, include errcode, status
	 */
	@ApiModelProperty("Header of response")
	private ResponseHeader header;

	/**
	 * Task id of request, using to check status of request and get the result
	 */
	@ApiModelProperty("The task id")
	private String task;

	/**
	 * Deafult constructor
	 */
	public TaskResponse() {
	}

	/**
	 * Main constructor
	 * @param status
	 * @param code
	 * @param errMss
	 * @param task
	 */
	public TaskResponse(String status, String errMss, String task) {
		this.header = new ResponseHeader(status, errMss);
		this.task = task;
	}

	/**
	 * @return the header
	 */
	public ResponseHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(ResponseHeader header) {
		this.header = header;
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
