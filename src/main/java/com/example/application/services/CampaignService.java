package com.example.application.services;

import com.example.application.data.Calendar;
import com.example.application.data.Campaign;
import com.example.application.data.User;
import com.example.application.data.World;
import com.example.application.data.repositories.CalendarRepository;
import com.example.application.data.repositories.CampaignRepository;
import com.example.application.data.repositories.WorldRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final CalendarRepository calendarRepository;
    private final WorldRepository worldRepository;

    public CampaignService(CampaignRepository campaignRepository, CalendarRepository calendarRepository, WorldRepository worldRepository) {
        this.campaignRepository = campaignRepository;
        this.calendarRepository = calendarRepository;
        this.worldRepository = worldRepository;
    }

    public List<Calendar> getCalendarsForUser(User user) {
        List<Campaign> campaigns = campaignRepository.findByGms(user);
        campaigns.removeAll(campaignRepository.findByPlayers(user));
        List<Calendar> calendars = new ArrayList<>();

        for (Campaign campaign : campaigns) {
            calendars.addAll(calendarRepository.findByCampaigns(campaign));
        }
        return calendars;
    }

    // TODO: Check if this is still an issue
    // TODO: Fix world list generation for user to be used in displaying worlds for campaign editing
    public List<World> getWorldsForUser(User user) {
        List<Campaign> campaigns = campaignRepository.findByGms(user);
        List<World> worlds = new ArrayList<>();

        for (Campaign campaign : campaigns) {
            worlds.addAll(worldRepository.findByCampaigns(campaign));
        }

        return worlds;
    }
}