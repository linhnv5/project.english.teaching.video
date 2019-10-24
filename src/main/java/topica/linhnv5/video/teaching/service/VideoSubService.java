package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.VideoTaskResult;
import topica.linhnv5.video.teaching.model.Task;

/**
 * Add sub service, provide function to make subbing video from input video
 * @author ljnk975
 */
public interface VideoSubService {

	/**
	 * Input a video file, chart name, artist name, add subtitle to result video
	 * @param inputFileName Mp4 file input name
	 * @param track         Track name
	 * @param artist        Main artist name
	 * @return              Task info of add sub video
	 * @throws VideoSubException
	 */
	public Task<VideoTaskResult> addSubToVideo(String track, String artist, String inputFileName, String inputSubFileName) throws VideoSubException;

	/**
	 * Input chart name, artist name, dowload subtitle, mp3 file and create a video subbing
	 * @param track  Track name
	 * @param artist Main Artist name
	 * @return       Task info of create mp3
	 * @throws VideoSubException
	 */
	public Task<VideoTaskResult> createSubVideoFromMusic(String track, String artist, String inputBackFileName, String inputSubFileName) throws VideoSubException;

	/**
	 * Input chart name, artist name, dowload subtitle
	 * @param track  Track name
	 * @param artist Main Artist name
	 * @return The subtitle model
	 * @throws VideoSubException
	 */
	public SongLyric getSubtitle(String track, String artist) throws VideoSubException;

}
