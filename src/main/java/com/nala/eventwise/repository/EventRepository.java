package com.nala.eventwise.repository;

import com.nala.eventwise.entity.Event;
import com.nala.eventwise.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);
}
