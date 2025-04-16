package com.example.application.data.repositories;

import com.example.application.data.Calendar;
import com.example.application.data.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByCampaigns(Campaign campaign);
}