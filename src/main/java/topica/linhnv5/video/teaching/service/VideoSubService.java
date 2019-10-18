package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
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
	 */
	public Task<SubVideoTaskResult> addSubToVideo(String inputFileName, String track, String artist) throws VideoSubException;

	/**
	 * Input chart name, artist name, dowload subtitle, mp3 file and create a video subbing
	 * @param track  Track name
	 * @param artist Main Artist name
	 * @return       Task info of create mp3
	 */
	public Task<SubVideoTaskResult> createSubVideoFromMusic(String track, String artist) throws VideoSubException;

}
