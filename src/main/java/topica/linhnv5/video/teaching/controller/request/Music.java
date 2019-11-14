package topica.linhnv5.video.teaching.controller.request;

import io.swagger.annotations.ApiParam;

/**
 * Request model hold information about chart name, artist name
 * 
 * @author ljnk975
 */
public class Music {

	/**
	 * Name of music video
	 */
	@ApiParam(value = "Track name(must be define if video metadata not have title)", required = false, example = "I do")
	private String title;

	/**
	 * Main artist name
	 */
	@ApiParam(value = "Artist name(must be define if video metadata not have artist)", required = false, example = "911")
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
