package topica.linhnv5.video.teaching.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.Task.Status;
import topica.linhnv5.video.teaching.service.TaskService;
import topica.linhnv5.video.teaching.service.VideoSubException;

@Service
public class TaskServiceImpl implements TaskService {

	/**
	 * List of task, hold all task
	 */
	private List<Task<?>> listOfTask = new ArrayList<Task<?>>();

	@Value("${tasks.queue.max}")
	private int taskMax;

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> Task<T> getTaskById(String id, Class<T> aClass) {
		for (Task<?> task : listOfTask) {
			if (task.getId().equals(id))
				return (Task<T>) task;
		}
		return null;
	}

	private boolean isContainTask(String id) {
		for (Task<?> task : listOfTask) {
			if (task.getId().equals(id))
				return true;
		}
		return false;
	}

	/**
	 * This function use when task list reach limit
	 * @return true if success
	 */
	private boolean freeTaskList() {
		for (int i = 0; i < this.listOfTask.size(); i++) {
			if (listOfTask.get(i).getStatus() == Status.FINISHED) {
				listOfTask.remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public synchronized void addTask(Task<?> task) throws VideoSubException {
		do {
			task.setId(UUID.randomUUID().toString());
		} while (isContainTask(task.getId()));
		if (this.listOfTask.size() > taskMax && !freeTaskList())
			throw new VideoSubException("Can't create new task, running task reach limit!");
		listOfTask.add(task);
	}

	
}
