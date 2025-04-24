package com.example.application.data.repositories;

import java.util.List;
import java.util.Optional;

import com.example.application.data.Campaign;
import com.example.application.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
    List<User> findByGmCampaigns(Campaign campaign);
    List<User> findByPlayerCampaigns(Campaign campaign);
}
