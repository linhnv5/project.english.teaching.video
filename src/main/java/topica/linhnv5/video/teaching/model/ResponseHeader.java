package topica.linhnv5.video.teaching.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * Header of request, include error code, err message
 * @author ljnk975
 */
public class ResponseHeader {

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
	 * Default constructor
	 */
	public ResponseHeader() {
	}

	/**
	 * Main constructor
	 * @param status
	 * @param code
	 * @param errMess
	 */
	public ResponseHeader(String status, String errMess) {
		this.status = status;
		this.mess = errMess;
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

}
