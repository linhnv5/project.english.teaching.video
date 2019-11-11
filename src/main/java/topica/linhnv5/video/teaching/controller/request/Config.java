package topica.linhnv5.video.teaching.controller.request;

import io.swagger.annotations.ApiParam;

/**
 * Config of output video
 * @author ljnk975
 */
public class Config {

	@ApiParam(value = "Width of output video", required = false)
	private int W;

	@ApiParam(value = "Height of output video", required = false)
	private int H;

	/**
	 * @return the w
	 */
	public int getW() {
		return W;
	}

	/**
	 * @param w the w to set
	 */
	public void setW(int w) {
		W = w;
	}

	/**
	 * @return the h
	 */
	public int getH() {
		return H;
	}

	/**
	 * @param h the h to set
	 */
	public void setH(int h) {
		H = h;
	}

}
