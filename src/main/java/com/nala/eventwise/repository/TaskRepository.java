package com.nala.eventwise.repository;

import com.nala.eventwise.entity.Task;
import com.nala.eventwise.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByEvent(Event event);
}
