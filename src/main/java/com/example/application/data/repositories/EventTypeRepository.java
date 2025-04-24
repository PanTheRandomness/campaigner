package com.example.application.data.repositories;

import com.example.application.data.EventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {
}
