package com.example.application.data.repositories;

import com.example.application.data.Campaign;
import com.example.application.data.World;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorldRepository extends JpaRepository<World, Long> {
    List<World> findByCampaigns(Campaign campaign);
}