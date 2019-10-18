package topica.linhnv5.video.teaching.zing.model;

import com.google.gson.annotations.SerializedName;

public class StreamData {

	@SerializedName("err")
	private int errCode;

	@SerializedName("msg")
	private String msg;

	@SerializedName("default")
	private StreamDefaultData data;

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
	public StreamDefaultData getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(StreamDefaultData data) {
		this.data = data;
	}

}
