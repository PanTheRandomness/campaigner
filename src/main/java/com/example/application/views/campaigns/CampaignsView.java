package com.example.application.views.campaigns;

import com.example.application.data.Calendar;
import com.example.application.data.Campaign;
import com.example.application.data.User;
import com.example.application.data.World;
import com.example.application.data.repositories.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.CampaignService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;

import java.util.*;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Campaigns")
@Route("campaigns")
@Menu(order = 3, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@PermitAll
public class CampaignsView extends Composite<VerticalLayout> implements AfterNavigationObserver {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final WorldRepository worldRepository;
    private final CalendarRepository calendarRepository;
    private final MoonRepository moonRepository;
    private final CampaignService campaignService;
    private final AuthenticatedUser authenticatedUser;

    private final Select<Campaign> campaignSelect = new Select<>();
    private final Tabs tabs = new Tabs();
    private final Map<Tab, Component> tabsToPages = new LinkedHashMap<>();
    private final VerticalLayout pages = new VerticalLayout();

    private final SplitLayout splitLayout = new SplitLayout();

    public CampaignsView(CampaignRepository campaignRepository, UserRepository userRepository, WorldRepository worldRepository, CalendarRepository calendarRepository, MoonRepository moonRepository, CampaignService campaignService, AuthenticatedUser authenticatedUser) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.worldRepository = worldRepository;
        this.calendarRepository = calendarRepository;
        this.moonRepository = moonRepository;
        this.campaignService = campaignService;
        this.authenticatedUser = authenticatedUser;

        HorizontalLayout layoutRow = new HorizontalLayout();
        Button createButton = new Button("Create New");
        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        getContent().setSpacing(false);
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow.setAlignItems(FlexComponent.Alignment.CENTER);
        layoutRow.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        campaignSelect.setLabel("Select Campaign");
        campaignSelect.setWidth("min-content");

        createButton.setWidth("min-content");
        createButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createButton.addClickListener(e -> openCampaignFormView(null));

        editButton.setWidth("min-content");
        editButton.addClickListener(e -> openCampaignFormView(campaignSelect.getValue()));
        editButton.setEnabled(false);

        deleteButton.setWidth("min-content");
        deleteButton.addClickListener(e -> confirmAndDeleteCampaign(campaignSelect.getValue()));
        deleteButton.setEnabled(false);

        campaignSelect.addValueChangeListener(event -> {
            Campaign selectedCampaign = event.getValue();
            boolean campaignSelected = selectedCampaign != null;
            editButton.setEnabled(campaignSelected);
            deleteButton.setEnabled(campaignSelected);

            if (campaignSelected) {
                updateTabs(selectedCampaign);
                UI.getCurrent().navigate("campaigns/campaign/" + selectedCampaign.getId() + "/overview");
            } else {
                tabs.removeAll();
                pages.removeAll();
            }
        });

        layoutRow.add(campaignSelect, createButton, editButton, deleteButton);
        getContent().add(layoutRow);

        splitLayout.setSizeFull();
        getContent().add(splitLayout);
        getContent().setFlexGrow(1, splitLayout);

        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSizeFull();
        leftSide.getStyle().set("flex-grow", "1");
        leftSide.add(tabs);
        pages.setSizeFull();
        pages.setAlignItems(FlexComponent.Alignment.START);
        pages.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        leftSide.add(pages);
        leftSide.setFlexGrow(1, pages);

        splitLayout.addToPrimary(leftSide);
        splitLayout.addToSecondary(new Div());

        initializeCampaigns();
    }

    private void initializeCampaigns() {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            showNoCampaignsView();
            return;
        }

        User user = maybeUser.get();
        Set<Campaign> campaigns = new HashSet<>();
        campaigns.addAll(user.getGmCampaigns());
        campaigns.addAll(user.getPlayerCampaigns());

        if (campaigns.isEmpty()) {
            showNoCampaignsView();
            return;
        }

        campaignSelect.setItems(campaigns);
        campaignSelect.setItemLabelGenerator(Campaign::getCampaignName);

        tabs.setVisible(false);
        pages.setVisible(false);
    }

    private void showNoCampaignsView() {
        tabs.removeAll();
        pages.removeAll();

        Tab overviewTab = new Tab("Overview");
        tabs.add(overviewTab);
        tabs.setSelectedTab(overviewTab);
        VerticalLayout messageLayout = new VerticalLayout();
        messageLayout.setWidthFull();
        messageLayout.setPadding(true);
        messageLayout.setHeightFull();

        Paragraph message = new Paragraph("You are not yet participating in any campaigns.");
        messageLayout.add(message);
        messageLayout.setAlignItems(FlexComponent.Alignment.START);

        pages.add(messageLayout);
    }

    private void updateTabs(Campaign campaign) {
        tabs.removeAll();
        pages.removeAll();
        tabsToPages.clear();

        Tab overviewTab = new Tab("Overview");
        Tab timelineTab = new Tab("Timeline");
        Tab playersTab = new Tab("Players");
        Tab worldTab = new Tab("World");

        tabs.add(overviewTab, timelineTab, playersTab, worldTab);

        Div overviewPage = new Div(new Paragraph("Overview content for " + campaign.getCampaignName()));
        Div timelinePage = new Div(new Paragraph("Timeline content for " + campaign.getCampaignName()));
        Div playersPage = new Div(new Paragraph("Players content for " + campaign.getCampaignName()));
        Div worldPage = new Div(new Paragraph("World content for " + campaign.getCampaignName()));

        tabsToPages.put(overviewTab, overviewPage);
        tabsToPages.put(timelineTab, timelinePage);
        tabsToPages.put(playersTab, playersPage);
        tabsToPages.put(worldTab, worldPage);

        pages.add(overviewPage, timelinePage, playersPage, worldPage);
    }

    private void openCampaignFormView(Campaign campaignToEdit) {
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isEmpty()) {
            showNoCampaignsView();
            return;
        }

        User user = maybeUser.get();
        Set<Campaign> campaigns = new HashSet<>();
        campaigns.addAll(user.getGmCampaigns());
        campaigns.addAll(user.getPlayerCampaigns());

        List<World> userWorlds = campaignService.getWorldsForUser(user);
        List<Calendar> userCalendars = campaignService.getCalendarsForUser(user);

        CampaignForm form = new CampaignForm(
                userRepository,
                worldRepository,
                calendarRepository,
                moonRepository,
                userWorlds,
                userCalendars,
                user,
                campaign -> {
                    campaignRepository.save(campaign);
                    UI.getCurrent().getPage().reload();
                },
                campaignToEdit
        );

        if (campaignToEdit != null) {
            form.fillFormWithCampaignData(campaignToEdit);
        }

        splitLayout.remove(splitLayout.getSecondaryComponent());
        splitLayout.addToSecondary(form);
    }

    private void confirmAndDeleteCampaign(Campaign campaign) {
        UI.getCurrent().getPage().executeJs("return confirm('Are you sure you want to delete this campaign?');")
                .then(Boolean.class, confirmDelete -> {
                    if (Boolean.TRUE.equals(confirmDelete)) {
                        campaignRepository.delete(campaign);
                        UI.getCurrent().getPage().reload();
                    }
                });
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String path = event.getLocation().getPath();
        if (path.startsWith("campaigns/campaign/")) {
            String[] segments = path.split("/");
            if (segments.length >= 4) {
                String campaignId = segments[2];
                String selectedTab = segments[3];

                Optional<Campaign> campaignOpt = campaignRepository.findById(Long.valueOf(campaignId));
                if (campaignOpt.isPresent()) {
                    Campaign selectedCampaign = campaignOpt.get();
                    campaignSelect.setValue(selectedCampaign);
                    updateTabs(selectedCampaign);

                    tabs.getChildren().forEach(tab -> {
                        if (tab instanceof Tab) {
                            Tab tabComponent = (Tab) tab;
                            if (tabComponent.getLabel().equalsIgnoreCase(selectedTab)) {
                                tabs.setSelectedTab(tabComponent);
                            }
                        }
                    });

                    tabsToPages.values().forEach(page -> page.setVisible(false));
                    Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
                    if (selectedPage != null) {
                        selectedPage.setVisible(true);
                    }
                } else {
                    showNoCampaignsView();
                }
            }
        }
    }
}
