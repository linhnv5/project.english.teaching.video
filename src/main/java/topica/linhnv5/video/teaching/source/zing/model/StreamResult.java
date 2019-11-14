package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class StreamResult {

	@SerializedName("err")
	private int errCode;

	@SerializedName("msg")
	private String msg;
	
	@SerializedName("data")
	private StreamData data;

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
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the data
	 */
	public StreamData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(StreamData data) {
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
