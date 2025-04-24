package com.example.application.data.repositories;

import com.example.application.data.Campaign;
import com.example.application.data.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    @Query("SELECT DISTINCT e.type FROM Event e WHERE e.campaign IN :campaigns")
    List<EventType> findDistinctEventTypesByCampaigns(@Param("campaigns") List<Campaign> campaigns);

}
