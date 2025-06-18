package com.nala.eventwise.controller;

import com.nala.eventwise.entity.Event;
import com.nala.eventwise.entity.User;
import com.nala.eventwise.repository.EventRepository;
import com.nala.eventwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Map<String, Object>> getMyEvents(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<Event> events = eventRepository.findByUser(user);

        List<Map<String, Object>> eventMaps = new ArrayList<>();
        for (Event event : events) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", event.getId());
            map.put("name", event.getName());
            map.put("description", event.getDescription());
            map.put("imageUrl", event.getImageUrl());
            eventMaps.add(map);
        }
        return eventMaps;
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

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@AuthenticationPrincipal UserDetails userDetails,
                                          @PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getUser().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", event.getId());
        map.put("name", event.getName());
        map.put("description", event.getDescription());
        map.put("imageUrl", event.getImageUrl());

        return ResponseEntity.ok(map);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long eventId,
                                         @RequestBody Event updatedEvent) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getUser().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        event.setName(updatedEvent.getName());
        event.setDescription(updatedEvent.getDescription());
        event.setImageUrl(updatedEvent.getImageUrl());

        eventRepository.save(event);

        return ResponseEntity.ok("Event updated successfully");
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getUser().getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access");
        }

        eventRepository.delete(event);
        return ResponseEntity.ok("Event deleted successfully");
    }

}
