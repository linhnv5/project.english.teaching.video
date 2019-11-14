package topica.linhnv5.video.teaching.source.zing.model;

import com.google.gson.annotations.SerializedName;

public class StreamDefaultData {

	@SerializedName("128")
	private String link128;
	
	@SerializedName("320")
	private String link320;

	/**
	 * @return the link128
	 */
	public String getLink128() {
		return link128;
	}

	/**
	 * @param link128 the link128 to set
	 */
	public void setLink128(String link128) {
		this.link128 = link128;
	}

	/**
	 * @return the link320
	 */
	public String getLink320() {
		return link320;
	}

	/**
	 * @param link320 the link320 to set
	 */
	public void setLink320(String link320) {
		this.link320 = link320;
	}

}
