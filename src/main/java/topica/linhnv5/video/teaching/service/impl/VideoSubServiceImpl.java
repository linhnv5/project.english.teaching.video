package topica.linhnv5.video.teaching.service.impl;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.SubVideoTaskResult;
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
	public Task<SubVideoTaskResult> addSubToVideo(String inputFileName, String track, String artist) {
		// Create task
		Task<SubVideoTaskResult> task = new Task<SubVideoTaskResult>();

		// Create async job
		Future<SubVideoTaskResult> future = execute.doAddSubToVideo(inputFileName, track, artist, task);

		// Set task
		task.setTask(future);

		// Add task
		taskService.addTask(task);

		// And just return it
		return task;
	}

	@Override
	public Task<SubVideoTaskResult> createSubVideoFromMusic(String track, String artist) throws VideoSubException {
		// Create task
		Task<SubVideoTaskResult> task = new Task<SubVideoTaskResult>();

		// Create async job
		Future<SubVideoTaskResult> future = execute.doCreateSubVideoFromMusic(track, artist, task);

		// Set task
		task.setTask(future);

		// Add task
		taskService.addTask(task);

		// And just return it
		return task;
	}

}
