package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final AccountRepository accountRepository;

    public TaskController(TaskRepository taskRepository, AccountRepository accountRepository) {
        this.taskRepository = taskRepository;
        this.accountRepository = accountRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        return "index";
    }

    @PostMapping("/addTask")
    public String addTask(@ModelAttribute Task task) {
        task.setOwner(currentAccount());
        taskRepository.save(task);
        return "redirect:/";
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @GetMapping("/deleteTask/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/api")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @PostMapping("/api")
    public ResponseEntity<Task> addTaskFromClient(@RequestBody Task task) {
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        task.setId(null);
        task.setCompleted(false);
        task.setOwner(currentAccount());
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PreAuthorize("hasAnyRole('MODERATOR', 'EDITOR')")
    @PostMapping("/updateTask/{id}")
    public String updateTask(@PathVariable Long id, @RequestParam boolean completed) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(completed);
        taskRepository.save(task);
        return "redirect:/";
    }

    private Account currentAccount() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByName(name).orElseGet(() -> {
            Account account = new Account();
            account.setName(name);
            account.setEmail(name + "@example.com");
            account.setPassword("{noop}password");
            return accountRepository.save(account);
        });
    }
}
