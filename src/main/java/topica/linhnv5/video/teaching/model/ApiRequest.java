package topica.linhnv5.video.teaching.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Request model hold information about chart name, artist name, video, type
 * request
 * 
 * @author ljnk975
 */
public class ApiRequest {

	/**
	 * Name of music video
	 */
	private String title;

	/**
	 * Main artist name
	 */
	private String artist;

	/**
	 * Music Video
	 */
	private MultipartFile video;

	/**
	 * If video is not specify, api will download music file and add background to video
	 */
	private MultipartFile background;

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
	 * @return the video
	 */
	public MultipartFile getVideo() {
		return video;
	}

	/**
	 * @param video the video to set
	 */
	public void setVideo(MultipartFile video) {
		this.video = video;
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

}
