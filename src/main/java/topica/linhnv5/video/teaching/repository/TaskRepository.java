package topica.linhnv5.video.teaching.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import topica.linhnv5.video.teaching.model.Task;

@Repository
public interface TaskRepository extends CrudRepository<Task, String> {

}
