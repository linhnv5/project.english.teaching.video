package topica.linhnv5.video.teaching.service.impl;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.TaskResult;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.service.VideoSubException;
import topica.linhnv5.video.teaching.service.VideoSubService;

@Service
public class VideoSubServiceImpl implements VideoSubService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private VideoSubExecute execute;

	@Override
	public Task<TaskResult> addSubToVideo(String track, String artist, String inputFileName, String inputSubFileName) throws VideoSubException {
		// Create task
		Task<TaskResult> task = new Task<TaskResult>();

		// Create async job
		Future<TaskResult> future = execute.doAddSubToVideo(track, artist, inputFileName, inputSubFileName, task);

		// Set task
		task.setTask(future);

		// Add task
		taskService.addTask(task);

		// And just return it
		return task;
	}

	@Override
	public Task<TaskResult> createSubVideoFromMusic(String track, String artist, String inputBackFileName, String inputMusicFileName, String inputSubFileName) throws VideoSubException {
		// Create task
		Task<TaskResult> task = new Task<TaskResult>();

		// Create async job
		Future<TaskResult> future = execute.doCreateSubVideoFromMusic(track, artist, inputBackFileName, inputMusicFileName, inputSubFileName, task);

		// Set task
		task.setTask(future);

		// Add task
		taskService.addTask(task);

		// And just return it
		return task;
	}

	@Override
	public SongLyric getSubtitle(String track, String artist) throws VideoSubException {
		SongLyric ret = null;

		try {
			ret = execute.doGetSubtitle(execute.doGetMatchingTrack(track, artist));
		} catch(Exception e) {
			throw new VideoSubException(e.getMessage());
		}

		return ret;
	}

}
