package topica.linhnv5.video.teaching.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

	/**
	 * List of task, hold all task
	 */
	private List<Task<?>> listOfTask = new ArrayList<Task<?>>();

	/**
	 * Id of next task
	 */
	private AtomicInteger idAutoIncrement = new AtomicInteger(0);

	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> Task<T> getTaskById(int id, Class<T> aClass) {
		for (Task<?> task : listOfTask) {
			if (task.getId() == id)
				return (Task<T>) task;
		}
		return null;
	}

	@Override
	public synchronized void addTask(Task<?> task) {
		task.setId(idAutoIncrement.incrementAndGet());
		listOfTask.add(task);
	}

	
}
