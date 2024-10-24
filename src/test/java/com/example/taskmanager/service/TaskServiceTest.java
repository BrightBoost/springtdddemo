package com.example.taskmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("This is a test task");
        task.setDueDate(LocalDate.now().plusDays(7));

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task createdTask = taskService.createTask(task);

        // Assert
        assertNotNull(createdTask);
        assertEquals("Test Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testGetAllTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", LocalDate.now().plusDays(1)),
                new Task("Task 2", "Description 2", LocalDate.now().plusDays(2))
        );

        when(taskRepository.findAll()).thenReturn(tasks);

        // Act
        List<Task> retrievedTasks = taskService.getAllTasks();

        // Assert
        assertEquals(2, retrievedTasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById() {
        // Arrange
        Task task = new Task("Task 1", "Description 1", LocalDate.now().plusDays(1));
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        Task retrievedTask = taskService.getTaskById(1L);

        // Assert
        assertNotNull(retrievedTask);
        assertEquals(1L, retrievedTask.getId());
        assertEquals("Task 1", retrievedTask.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }



    @Test
    void testUpdateTaskStatus() {
        // Arrange
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Existing Task");
        task.setStatus("To Do");

        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task updatedTask = taskService.updateTaskStatus(1L, "In Progress");

        // Assert
        assertNotNull(updatedTask);
        assertEquals("In Progress", updatedTask.getStatus());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void testUpdateTaskStatus_TaskNotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskStatus(1L, "In Progress"));
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(0)).save(any(Task.class));
    }

}
