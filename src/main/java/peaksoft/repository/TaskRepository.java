package peaksoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import peaksoft.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
