package com.example.application.views.campaigns;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.HashMap;
import java.util.Map;

@Route("campaigns/campaign/:campaignId/:tabName")
@PermitAll
public class CampaignDetailsView extends VerticalLayout {
    private String campaignId;
    private String tabName;

    private final Map<Tab, Div> tabsToPages = new HashMap<>();
    private Tabs tabs = new Tabs();
    private Div pages = new Div();

    public CampaignDetailsView() {
        campaignId = getElement().getAttribute("campaignId");
        tabName = getElement().getAttribute("tabName");

        Tab overviewTab = new Tab("Overview");
        Tab timelineTab = new Tab("Timeline");
        Tab playersTab = new Tab("Players");
        Tab worldTab = new Tab("World");

        tabs.add(overviewTab, timelineTab, playersTab, worldTab);

        Div overviewPage = new Div(new Paragraph("Overview content for " + campaignId));
        Div timelinePage = new Div(new Paragraph("Timeline content for " + campaignId));
        Div playersPage = new Div(new Paragraph("Players content for " + campaignId));
        Div worldPage = new Div(new Paragraph("World content for " + campaignId));

        tabsToPages.put(overviewTab, overviewPage);
        tabsToPages.put(timelineTab, timelinePage);
        tabsToPages.put(playersTab, playersPage);
        tabsToPages.put(worldTab, worldPage);

        pages.add(overviewPage, timelinePage, playersPage, worldPage);

        add(tabs, pages);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            if (selectedPage != null) {
                selectedPage.setVisible(true);
            }

            String tabText = tabs.getSelectedTab().getLabel().toLowerCase();
            UI.getCurrent().navigate("campaigns/campaign/" + campaignId + "/" + tabText);
        });

        Tab initialTab = tabsToPages.keySet().stream()
                .filter(tab -> tab.getLabel().equalsIgnoreCase(tabName))
                .findFirst()
                .orElse(overviewTab);
        tabs.setSelectedTab(initialTab);

        tabsToPages.values().forEach(page -> page.setVisible(false));
        tabsToPages.get(initialTab).setVisible(true);
    }
}