package topica.linhnv5.video.teaching.model;

/**
 * Response for subbing video service, include task id, error code, ...
 * @author ljnk975
 */
public class ApiResponse {

	/**
	 * Header of request, include errcode, status
	 */
	private ApiResponseHeader header;

	/**
	 * Task id of request, using to check status of request and get the result
	 */
	private int task;

	/**
	 * Deafult constructor
	 */
	public ApiResponse() {
	}

	/**
	 * Main constructor
	 * @param status
	 * @param code
	 * @param errMss
	 * @param task
	 */
	public ApiResponse(String status, int code, String errMss, int task) {
		this.header = new ApiResponseHeader(status, code, errMss);
		this.task = task;
	}

	/**
	 * @return the header
	 */
	public ApiResponseHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(ApiResponseHeader header) {
		this.header = header;
	}

	/**
	 * @return the task
	 */
	public int getTask() {
		return task;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(int task) {
		this.task = task;
	}

	
}
