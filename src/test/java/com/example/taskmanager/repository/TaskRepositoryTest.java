package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testSaveAndFindTask() {
        // Arrange
        Task task = new Task();
        task.setTitle("Repository Test Task");
        task.setDescription("Testing repository save and find");
        task.setDueDate(LocalDate.now().plusDays(5));

        // Act
        Task savedTask = taskRepository.save(task);
        Task foundTask = taskRepository.findById(savedTask.getId()).orElse(null);

        // Assert
        assertNotNull(foundTask);
        assertEquals(savedTask.getTitle(), foundTask.getTitle());
    }

    @Test
    void testFindAllTasks() {
        // Arrange
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setDescription("First task");
        taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setDescription("Second task");
        taskRepository.save(task2);

        // Act
        List<Task> tasks = taskRepository.findAll();

        // Assert
        assertEquals(2, tasks.size());
    }
}
