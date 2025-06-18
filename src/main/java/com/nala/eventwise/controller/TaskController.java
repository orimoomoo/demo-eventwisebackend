package com.nala.eventwise.controller;

import com.nala.eventwise.entity.Event;
import com.nala.eventwise.entity.Task;
import com.nala.eventwise.entity.User;
import com.nala.eventwise.repository.EventRepository;
import com.nala.eventwise.repository.TaskRepository;
import com.nala.eventwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    // Securely view tasks for an event (owned by the user)
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getTasksByEvent(@PathVariable Long eventId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Event event = eventRepository.findById(eventId).orElseThrow();

        if (!event.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not own this event.");
        }

        List<Task> tasks = taskRepository.findByEvent(event);
        return ResponseEntity.ok(tasks);
    }

    // Add task to a specific event
    @PostMapping("/{eventId}")
    public ResponseEntity<String> addTask(@PathVariable Long eventId, @RequestBody Task task) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Event event = eventRepository.findById(eventId).orElseThrow();

        // MUST HAVE, Protect against adding tasks to other users events
        if (!event.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("You do not own this event.");
        }

        task.setEvent(event);
        taskRepository.save(task);
        return ResponseEntity.ok("Task added to event");
    }

    // Update task
    @PutMapping("/{taskId}")
    public ResponseEntity<String> updateTask(@PathVariable Long taskId, @RequestBody Task updatedTask) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setName(updatedTask.getName());
        task.setDescription(updatedTask.getDescription());
        task.setCost(updatedTask.getCost());
        task.setCompleted(updatedTask.getCompleted());
        taskRepository.save(task);
        return ResponseEntity.ok("Task updated");
    }

    // Delete task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskRepository.deleteById(taskId);
        return ResponseEntity.ok("Task deleted");
    }

    // Update event
    @PutMapping("/event/{eventId}")
    public ResponseEntity<String> updateEvent(@PathVariable Long eventId, @RequestBody Event updatedEvent) {
        Event event = eventRepository.findById(eventId).orElseThrow();

        event.setName(updatedEvent.getName());
        event.setDescription(updatedEvent.getDescription());
        event.setImageUrl(updatedEvent.getImageUrl());
        eventRepository.save(event);

        return ResponseEntity.ok("Event updated");
    }

    // Delete event
    @DeleteMapping("/event/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId) {
        eventRepository.deleteById(eventId);
        return ResponseEntity.ok("Event deleted");
    }

    // Get endpoint to Fetch Task by ID
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        return ResponseEntity.ok(task);
    }


}
