package topica.linhnv5.video.teaching.model;

import java.io.File;

/**
 * The result of subbing task
 * @author ljnk975
 */
public class TaskResult {

	/**
	 * The output file
	 */
	private File output;

	/**
	 * Exception occur in progress
	 */
	private Exception exception;

	public TaskResult() {
	}

	/**
	 * @return the output
	 */
	public File getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(File output) {
		this.output = output;
	}

	/**
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}

}
