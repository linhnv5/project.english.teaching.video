package topica.linhnv5.video.teaching.service;

import topica.linhnv5.video.teaching.model.Task;
import topica.linhnv5.video.teaching.model.TaskExecute;

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
	public Task getTaskById(String id);

	/**
	 * Get execute task by id
	 * @param id id of task
	 * @return the task
	 */
	public TaskExecute getTaskExecuteById(String id);

	/**
	 * Add a task
	 * @param execute the task execute
	 */
	public void addTask(TaskExecute execute);

	/**
	 * Save task info
	 * @param task the task
	 */
	public void saveTask(Task task);

}
