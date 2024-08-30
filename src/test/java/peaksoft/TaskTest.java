package peaksoft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import peaksoft.entity.Task;
import peaksoft.exception.NotFoundException;
import peaksoft.message.SimpleResponse;
import peaksoft.repository.TaskRepository;
import peaksoft.service.impl.TaskServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TaskTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task();
        task.setId(1L);
        task.setDescription("Test Task");
        task.setCompleted(false);
    }

    /**
     * Проверка получения всех задач.
     */
    @Test
    public void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Collections.singletonList(task));

        assertEquals(1, taskService.getAllTasks().size());
        verify(taskRepository, times(1)).findAll();
    }

    /**
     * Проверка получения задачи по ID, если задача существует.
     */
    @Test
    public void testGetTaskByIdFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        Task result = taskService.getTaskById(2L);
        assertEquals(task, result);
        verify(taskRepository, times(1)).findById(anyLong());
    }

    /**
     * Проверка получения задачи по ID, если задача не найдена.
     */
    @Test
    public void testGetTaskByIdNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> taskService.getTaskById(1L));
        assertEquals("Task with id : 1 not found", thrown.getMessage());
        verify(taskRepository, times(1)).findById(anyLong());
    }

    /**
     * Проверка создания задачи.
     */
    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);
        assertEquals(task, createdTask);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Проверка обновления задачи, если задача существует.
     */
    @Test
    public void testUpdateTaskFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task updatedTask = taskService.updateTask(1L, task);
        assertEquals(task, updatedTask);
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Проверка обновления задачи, если задача не найдена.
     */
    @Test
    public void testUpdateTaskNotFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> taskService.updateTask(1L, task));
        assertEquals("Task not found with id: 1", thrown.getMessage());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(0)).save(any(Task.class));
    }

    /**
     * Проверка удаления задачи, если задача существует.
     */
    @Test
    public void testDeleteTaskFound() {
        when(taskRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(taskRepository).deleteById(anyLong());

        SimpleResponse response = taskService.deleteTask(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.httpStatus());
        assertEquals("Task with id: 1 was removed", response.message());
        verify(taskRepository, times(1)).existsById(anyLong());
        verify(taskRepository, times(1)).deleteById(anyLong());
    }

    /**
     * Проверка удаления задачи, если задача не найдена.
     */
    @Test
    public void testDeleteTaskNotFound() {
        when(taskRepository.existsById(anyLong())).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> taskService.deleteTask(1L));
        assertEquals("Task with id: 1 is not found", thrown.getMessage());
        verify(taskRepository, times(1)).existsById(anyLong());
        verify(taskRepository, times(0)).deleteById(anyLong());
    }
}
