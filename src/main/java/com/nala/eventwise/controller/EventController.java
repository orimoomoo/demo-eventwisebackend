package com.nala.eventwise.controller;

import com.nala.eventwise.entity.Event;
import com.nala.eventwise.entity.User;
import com.nala.eventwise.repository.EventRepository;
import com.nala.eventwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin("*")
public class EventController {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<String> createEvent(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody Event event) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        event.setUser(user);
        eventRepository.save(event);
        return ResponseEntity.ok("Event created successfully");
    }

    @GetMapping
    public List<Event> getMyEvents(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return eventRepository.findByUser(user);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getEventsByUserId(@AuthenticationPrincipal UserDetails userDetails,
                                               @PathVariable Long userId) {
        User requester = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        if (!requester.getId().equals(userId)) {
            return ResponseEntity.status(403).body("You are not authorized to view this user's events.");
        }

        User targetUser = userRepository.findById(userId).orElseThrow();
        List<Event> events = eventRepository.findByUser(targetUser);

        return ResponseEntity.ok(events);
    }
}
