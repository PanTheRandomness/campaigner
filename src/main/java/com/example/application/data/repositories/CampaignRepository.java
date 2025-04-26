package com.example.application.data.repositories;

import com.example.application.data.Campaign;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findByGms(User user);

    List<Campaign> findByPlayers(User user);

}
