package com.example.taskmanager.controller;

import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTask() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle("Controller Test Task");
        task.setDescription("Testing controller");
        task.setDueDate(LocalDate.now().plusDays(3));

        when(taskService.createTask(any(Task.class))).thenReturn(task);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Controller Test Task"));
    }

    @Test
    void testCreateTask_InvalidInput() throws Exception {
        // Arrange
        Task task = new Task();
        task.setTitle(""); // Invalid title

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Arrange
        List<Task> tasks = Arrays.asList(
                new Task("Task 1", "Description 1", LocalDate.now().plusDays(1)),
                new Task("Task 2", "Description 2", LocalDate.now().plusDays(2))
        );

        when(taskService.getAllTasks()).thenReturn(tasks);

        // Act & Assert
        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    void testGetTaskById() throws Exception {
        // Arrange
        Task task = new Task("Task 1", "Description 1", LocalDate.now().plusDays(1));
        task.setId(1L);

        when(taskService.getTaskById(1L)).thenReturn(task);

        // Act & Assert
        mockMvc.perform(get("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Task 1"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenThrow(new TaskNotFoundException("Task not found"));

        // Act & Assert
        mockMvc.perform(get("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }



}

