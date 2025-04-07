package com.example.application.data.repositories;

import com.example.application.data.EventDuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDurationRepository extends JpaRepository<EventDuration, Long> {
}
