package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.model.Task;

/**
 * Task service, provide function to create and manage task
 * @author ljnk975
 */
public interface TaskService {

	/**
	 * Get task by id
	 * @param id id of task
	 * @return   The task
	 */
	public <T> Task<T> getTaskById(String id, Class<T> aClass);

	/**
	 * Add a task
	 * @param task the task
	 */
	public void addTask(Task<?> task) throws VideoSubException;

}
