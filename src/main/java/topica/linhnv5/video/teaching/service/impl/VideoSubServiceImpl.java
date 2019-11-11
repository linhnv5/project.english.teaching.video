package topica.linhnv5.video.teaching.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.lyric.SongLyric;
import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskExecute;
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
	public Task addSubToVideo(String track, String artist, String inputFileName, String inputSubFileName) throws VideoSubException {
		// Create task
		TaskExecute task = new TaskExecute();

		try {
			// Add task
			taskService.addTask(task);

			// Create async job
			execute.doAddSubToVideo(track, artist, inputFileName, inputSubFileName, task);
		} catch(Exception e) {
			e.printStackTrace();
			throw new VideoSubException(e.getMessage());
		}

		// And just return it
		return task.getTask();
	}

	@Override
	public Task createSubVideoFromMusic(String track, String artist, String inputBackFileName, String inputMusicFileName, String inputSubFileName) throws VideoSubException {
		// Create task
		TaskExecute task = new TaskExecute();

		try {
			// Add task
			taskService.addTask(task);

			// Create async job
			execute.doCreateSubVideoFromMusic(track, artist, inputBackFileName, inputMusicFileName, inputSubFileName, task);
		} catch(Exception e) {
			e.printStackTrace();
			throw new VideoSubException(e.getMessage());
		}

		// And just return it
		return task.getTask();
	}

	@Override
	public SongLyric getSubtitle(String track, String artist) throws VideoSubException {
		SongLyric ret = null;

		try {
			ret = execute.doGetSubtitle(track, artist);
		} catch(Exception e) {
			throw new VideoSubException(e.getMessage());
		}

		return ret;
	}

}
