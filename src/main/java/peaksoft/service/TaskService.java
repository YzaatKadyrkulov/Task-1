package peaksoft.service;

import peaksoft.entity.Task;
import peaksoft.message.SimpleResponse;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task createTask(Task task);

    Task updateTask(Long id, Task task);

    SimpleResponse deleteTask(Long id);
}
