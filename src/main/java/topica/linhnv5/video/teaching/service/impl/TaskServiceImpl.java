package topica.linhnv5.video.teaching.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskExecute;
import topica.linhnv5.video.teaching.repository.TaskRepository;
import topica.linhnv5.video.teaching.service.TaskService;

/**
 * Task service, execute task
 * @author ljnk975
 */
@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public Task getTaskById(String id) {
		Optional<Task> ret = taskRepository.findById(id);
		return ret.isPresent() ? ret.get() : null;
	}

	/**
	 * List of task, hold all task
	 */
	private List<TaskExecute> listOfTask = new ArrayList<>();

	@Override
	public synchronized TaskExecute getTaskExecuteById(String id) {
		for (TaskExecute task : listOfTask) {
			if (task.getTask().getId().equals(id))
				return task;
		}
		return null;
	}

	@Override
	public void addTask(TaskExecute execute) {
		Task task = execute.getTask();
		do {
			task.setId(UUID.randomUUID().toString());
		} while (getTaskById(task.getId()) != null);
		listOfTask.add(execute);
		taskRepository.save(task);
	}

	@Override
	public void saveResult(TaskExecute execute) {
		listOfTask.remove(execute);
		taskRepository.save(execute.getTask());
	}

}
