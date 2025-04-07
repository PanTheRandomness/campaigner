package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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

    //TODO: Add GM (User) & relation
    //TODO: Add Player & M2M-relation

    //TODO: Add NPCs, Organizations, Monsters, Items

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
}
