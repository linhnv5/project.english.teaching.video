package topica.linhnv5.video.teaching.model;

import io.swagger.annotations.ApiParam;

/**
 * Request model hold information about chart name, artist name, video, type
 * request
 * 
 * @author ljnk975
 */
public class TaskRequest {

	/**
	 * Name of music video
	 */
	@ApiParam(required = false, value = "Track name(must be define if video metadata not have title)")
	private String title;

	/**
	 * Main artist name
	 */
	@ApiParam(required = false, value = "Artist name(must be define if video metadata not have artist)")
	private String artist;

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

}
