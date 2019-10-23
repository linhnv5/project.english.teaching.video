package topica.linhnv5.video.teaching.model;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;

/**
 * Request model hold information about chart name, artist name, video, type
 * request
 * 
 * @author ljnk975
 */
public class TaskImageRequest {

	/**
	 * Name of music video
	 */
	@ApiModelProperty("Track name")
	private String title;

	/**
	 * Main artist name
	 */
	@ApiModelProperty("Artist name")
	private String artist;

	/**
	 * If video is not specify, api will download music file and add background to video
	 */
	@ApiModelProperty("Background of output video(image or short video)")
	private MultipartFile background;

	/**
	 * Sub file
	 */
	@ApiModelProperty("Subtitle file (csv)")
	private MultipartFile sub;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the background
	 */
	public MultipartFile getBackground() {
		return background;
	}

	/**
	 * @param background the background to set
	 */
	public void setBackground(MultipartFile background) {
		this.background = background;
	}

	/**
	 * @return the sub
	 */
	public MultipartFile getSub() {
		return sub;
	}

	/**
	 * @param sub the sub to set
	 */
	public void setSub(MultipartFile sub) {
		this.sub = sub;
	}

}
