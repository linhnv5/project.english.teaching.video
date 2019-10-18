package topica.linhnv5.video.teaching.model;

/**
 * Header of request, include error code, err message
 * @author ljnk975
 */
public class ApiResponseHeader {

	/**
	 * Status of request, SUCCESS if command return OK, ERROR if there are some error occur
	 */
	private String status;

	/**
	 * Request code, 200 for OK
	 */
	private int code;

	/**
	 * Request error message
	 */
	private String errMess;

	/**
	 * Default constructor
	 */
	public ApiResponseHeader() {
	}

	/**
	 * Main constructor
	 * @param status
	 * @param code
	 * @param errMess
	 */
	public ApiResponseHeader(String status, int code, String errMess) {
		this.status = status;
		this.code = code;
		this.errMess = errMess;
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
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the errMess
	 */
	public String getErrMess() {
		return errMess;
	}

	/**
	 * @param errMess the errMess to set
	 */
	public void setErrMess(String errMess) {
		this.errMess = errMess;
	}

}
