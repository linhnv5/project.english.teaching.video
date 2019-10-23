package topica.linhnv5.video.teaching.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.service.TaskService;

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
			System.out.println("ASD "+task.getId()+" "+id+" eq="+task.getId().equals(id));
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

	@Override
	public synchronized void addTask(Task<?> task) {
		do {
			task.setId(UUID.randomUUID().toString());
		} while (isContainTask(task.getId()));
		if (this.listOfTask.size() > taskMax)
			listOfTask.remove(0);
		listOfTask.add(task);
	}

	
}
