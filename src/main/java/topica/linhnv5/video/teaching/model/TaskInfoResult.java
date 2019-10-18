package topica.linhnv5.video.teaching.model;

/**
 * Result for api get task status
 * @author ljnk975
 */
public class TaskInfoResult {

	private String status;
	private String error;
	private int progress;

	public TaskInfoResult() {
	}
	
	public TaskInfoResult(String status, String error, int progress) {
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

}
