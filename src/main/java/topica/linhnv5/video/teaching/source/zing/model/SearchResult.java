package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class SearchResult {

	@SerializedName("err")
	private int errCode;

	@SerializedName("mss")
	private String message;

	@SerializedName("data")
	private SearchData data;

	@SerializedName("timestamp")
	private long timestamp;

	/**
	 * @return the errCode
	 */
	public int getErrCode() {
		return errCode;
	}

	/**
	 * @param errCode the errCode to set
	 */
	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the data
	 */
	public SearchData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(SearchData data) {
		this.data = data;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
