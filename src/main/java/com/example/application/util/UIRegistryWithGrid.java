package com.example.application.util;

import com.example.application.data.Campaign;
import com.example.application.data.Event;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class UIRegistryWithGrid {

    public static class UIWithGrid {
        private final UI ui;
        private final Grid<Event> eventGrid;
        private final Long campaignId;

        public UIWithGrid(UI ui, Grid<Event> eventGrid, Long campaignId) {
            this.ui = ui;
            this.eventGrid = eventGrid;
            this.campaignId = campaignId;
        }

        public UI getUi() {
            return ui;
        }

        public Grid<Event> getEventGrid() {
            return eventGrid;
        }

        public Long getCampaignId() {
            return campaignId;
        }
    }

    private static final Set<UIWithGrid> activeUIs = new CopyOnWriteArraySet<>();

    public static void register(UI ui, Grid<Event> eventGrid, Campaign campaign) {
        activeUIs.add(new UIWithGrid(ui, eventGrid, campaign.getId()));
        ui.addDetachListener(event -> activeUIs.removeIf(u -> u.getUi().equals(ui)));
    }

    public static Set<UIWithGrid> getActiveUIs() {
        return activeUIs;
    }
}
