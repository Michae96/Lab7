package com.example.taskapp.controllers;

import com.example.taskapp.config.TaskRepository;
import com.example.taskapp.config.CategoryRepository;
import com.example.taskapp.config.UserRepository;
import com.example.taskapp.models.Task;
import com.example.taskapp.models.Category;
import com.example.taskapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listTasks(@RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "") String status,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model,
                            @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

        Pageable pageable = PageRequest.of(page, size);

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Page<Task> tasks;

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            if (status.isEmpty()) {
                tasks = taskRepository.findByTitleContaining(search, pageable);
            } else {
                tasks = taskRepository.findByTitleContainingAndStatus(search, status, pageable);
            }
        } else {
            if (status.isEmpty()) {
                tasks = taskRepository.findByTitleContainingAndUser(search, user, pageable);
            } else {
                tasks = taskRepository.findByTitleContainingAndStatusAndUser(search, status, user, pageable);
            }
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "tasks";
    }

    @GetMapping("/add")
    public String showAddTaskPage(Model model) {
        model.addAttribute("task", new Task());
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        task.setUser(user);

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit-task/{id}")
    public String showEditTaskPage(@PathVariable Long id, Model model,
                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or you don't have access"));

        model.addAttribute("task", task);
        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "edit-task";
    }

    @PostMapping("/edit-task/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        task.setId(id);

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable Long id,
                             @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

        User user = userRepository.findByUsername(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Task task = taskRepository.findById(id)
                .filter(t -> t.getUser().equals(user))
                .orElseThrow(() -> new IllegalArgumentException("Task not found or you don't have access"));

        taskRepository.delete(task);
        return "redirect:/tasks";
    }
}
