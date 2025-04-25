package com.example.application.data.repositories;

import com.example.application.data.Campaign;
import com.example.application.data.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    // TODO: FIX these! They don't work
    @Query("SELECT e.type FROM Event e WHERE e.campaign IN :campaigns")
    List<EventType> findEventTypesByCampaigns(@Param("campaigns") List<Campaign> campaigns);

    @Query("SELECT e.type FROM Event e WHERE e.campaign = :campaign")
    List<EventType> findEventTypesByCampaign(@Param("campaign") Campaign campaign);
}
