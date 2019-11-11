package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.model.Music;

/**
 * Music service, provide function to get the music from music source
 * @author ljnk975
 */
public interface MusicService {

	/**
	 * Get the music by given artist and chart name
	 * @param chart  chart name
	 * @param artist artist name
	 * @return the music
	 */
	public Music getMusic(String chart, String artist);

}
