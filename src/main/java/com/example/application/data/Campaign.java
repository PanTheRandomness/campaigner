package com.example.application.data;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Campaign extends AbstractEntity {

    private String campaignName;

    private String campaignDescription;

    @ManyToOne
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    @ManyToOne
    @JoinColumn(name = "world_id")
    private World campaignWorld;

    @OneToMany(mappedBy = "campaign")
    private List<Event> events;

    @ManyToMany
    @JoinTable(
            name = "campaign_gms",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> gms;

    @ManyToMany
    @JoinTable(
            name = "campaign_players",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> players;

    //TODO: Add PCs, NPCs, Organizations, Monsters, Items

    // Getters & Setters

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getCampaignDescription() {
        return campaignDescription;
    }

    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public World getCampaignWorld() {
        return campaignWorld;
    }

    public void setCampaignWorld(World campaignWorld) {
        this.campaignWorld = campaignWorld;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<User> getGms() {
        return gms;
    }

    public void setGms(List<User> gms) {
        this.gms = gms;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }
}
