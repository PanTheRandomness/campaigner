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
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
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
public class CampaignsView extends Composite<VerticalLayout> {

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
        campaignSelect.addValueChangeListener(event -> updateTabs(event.getValue()));
        campaignSelect.setValue(campaigns.iterator().next());
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

        Tab overviewTab = new Tab(VaadinIcon.ARCHIVE.create(), new Span("Overview"));
        Tab timelineTab = new Tab(VaadinIcon.ROAD_BRANCHES.create(), new Span("Timelines"));
        Tab playersTab = new Tab(VaadinIcon.USERS.create(), new Span("Players"));
        Tab worldTab = new Tab(VaadinIcon.GLOBE.create(), new Span("World"));

        tabs.add(overviewTab, timelineTab, playersTab, worldTab);

        // TODO: Customize all Tab Content
        Div overviewPage = new Div(new Paragraph("Overview content for " + campaign.getCampaignName()));

        VerticalLayout timelinesLayout = new VerticalLayout();
        HorizontalLayout timelineLayout = new HorizontalLayout();
        HorizontalLayout eventsLayout = new HorizontalLayout();

        // TODO: Add content to Events
        // TODO: Relocate Events view to here
        timelinesLayout.add(timelineLayout, eventsLayout);

        Div timelinePage = new Div(new Paragraph("Timeline content for " + campaign.getCampaignName()));

        // Grid for Players & GMs
        H3 playerGridTitle = new H3(campaign.getCampaignName() + "'s GMs & players");
        Grid<User> playerGrid = new Grid<>(User.class, false);
        List<User> gms = userRepository.findByGmCampaigns(campaign);
        List<User> players = userRepository.findByPlayerCampaigns(campaign);

        // TODO: Fix playerGrid height!
        playerGrid.setWidth("100%");
        playerGrid.setHeight("100%");
        playerGrid.getStyle().set("font-size", "var(--lumo-font-size-l)");

        playerGrid.addColumn(User::getUsername)
                .setHeader("Username")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        playerGrid.addColumn(User::getName)
                .setHeader("Name")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        // Map user roles to be "GM" or "Player"
        Map<User, String> roleMap = new LinkedHashMap<>();
        gms.forEach(gm -> roleMap.put(gm, "GM"));
        players.forEach(player -> roleMap.put(player, "Player"));

        playerGrid.addColumn(user -> roleMap.get(user))
                .setHeader("Role")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);

        Map<User, String> characterMap = new LinkedHashMap<>();
        gms.forEach(gm -> characterMap.put(gm, "-"));
        // TODO: Add Characters when Character Entities have been added

        playerGrid.addColumn(user -> characterMap.get(user))
                .setHeader("Character")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.CENTER);

        playerGrid.setItems(roleMap.keySet());

        VerticalLayout playersLayout = new VerticalLayout();
        playersLayout.setSizeFull();
        playersLayout.setPadding(false);
        playersLayout.setSpacing(false);
        playersLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        playersLayout.add(playerGridTitle, playerGrid);

        Div playersPage = new Div(playersLayout);
        playersPage.setSizeFull();

        Div worldPage = new Div(new Paragraph("World content for " + campaign.getCampaignName()));

        tabsToPages.put(overviewTab, overviewPage);
        tabsToPages.put(timelineTab, timelinePage);
        tabsToPages.put(playersTab, playersPage);
        tabsToPages.put(worldTab, worldPage);

        pages.add(overviewPage, timelinePage, playersPage, worldPage);
        pages.setSizeFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            if (selectedPage != null) {
                selectedPage.setVisible(true);
            }
        });

        // Show the first tab by default
        tabs.setSelectedTab(overviewTab);
        tabsToPages.values().forEach(page -> page.setVisible(false));
        overviewPage.setVisible(true);
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
}