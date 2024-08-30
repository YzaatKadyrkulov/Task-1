package peaksoft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import peaksoft.entity.Task;
import peaksoft.exception.NotFoundException;
import peaksoft.message.SimpleResponse;
import peaksoft.repository.TaskRepository;
import peaksoft.service.TaskService;

import java.util.List;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");
        List<Task> tasks = taskRepository.findAll();
        log.info("Found {} tasks", tasks.size());
        return tasks;
    }

    @Override
    public Task getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task with id : " + id + " not found"));
    }

    @Override
    public Task createTask(Task task) {
        log.info("Creating new task with description: {}", task.getDescription());
        Task createdTask = taskRepository.save(task);
        log.info("Created task with id: {}", createdTask.getId());
        return createdTask;
    }

    @Override
    public Task updateTask(Long id, Task newTask) {
        log.info("Updating task with id: {}", id);
        return taskRepository.findById(id)
                .map(oldTask -> {
                    oldTask.setDescription(newTask.getDescription());
                    oldTask.setCompleted(newTask.isCompleted());
                    Task updatedTask = taskRepository.save(oldTask);
                    log.info("Updated task with id: {}", id);
                    return updatedTask;
                })
                .orElseThrow(() -> {
                    log.error("Task not found with id: {}", id);
                    return new NotFoundException("Task not found with id: " + id);
                });
    }

    @Override
    public SimpleResponse deleteTask(Long id) {
        log.info("Removing task with id: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Task with id: {} not found for deletion", id);
            throw new NotFoundException("Task with id: " + id + " is not found");
        }

        taskRepository.deleteById(id);
        log.info("Removed task with id: {}", id);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.NO_CONTENT)
                .message("Task with id: " + id + " was removed")
                .build();
    }
}
