package com.example.application.views.campaigns;

import com.example.application.data.*;
import com.example.application.data.Calendar;
import com.example.application.data.Event;
import com.example.application.data.repositories.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.services.CampaignService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;

import java.awt.*;
import java.util.*;
import java.util.List;

import org.vaadin.addons.tatu.ColorPicker;
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
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventDurationRepository eventDurationRepository;
    private final PlaceRepository placeRepository;

    private final CampaignService campaignService;
    private final AuthenticatedUser authenticatedUser;

    private final Select<Campaign> campaignSelect = new Select<>();
    private final Tabs tabs = new Tabs();
    private final Map<Tab, Component> tabsToPages = new LinkedHashMap<>();
    private final VerticalLayout pages = new VerticalLayout();

    private final SplitLayout splitLayout = new SplitLayout();

    public CampaignsView(CampaignRepository campaignRepository, UserRepository userRepository,
                         WorldRepository worldRepository, CalendarRepository calendarRepository,
                         MoonRepository moonRepository, EventRepository eventRepository,
                         EventTypeRepository eventTypeRepository, EventDurationRepository eventDurationRepository,
                         PlaceRepository placeRepository,
                         CampaignService campaignService, AuthenticatedUser authenticatedUser) {
        this.campaignRepository = campaignRepository;
        this.userRepository = userRepository;
        this.worldRepository = worldRepository;
        this.calendarRepository = calendarRepository;
        this.moonRepository = moonRepository;
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.eventDurationRepository = eventDurationRepository;
        this.placeRepository = placeRepository;

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
        // TODO: Fix Grid Sizes!!
        tabs.removeAll();
        pages.removeAll();
        tabsToPages.clear();

        Optional<User> maybeUser = authenticatedUser.get();
        User loggedUser = maybeUser.get();

        Tab overviewTab = new Tab(VaadinIcon.ARCHIVE.create(), new Span("Overview"));
        Tab timelineTab = new Tab(VaadinIcon.ROAD_BRANCHES.create(), new Span("Timelines"));
        Tab playersTab = new Tab(VaadinIcon.USERS.create(), new Span("Players"));
        Tab worldTab = new Tab(VaadinIcon.GLOBE.create(), new Span("World"));

        tabs.add(overviewTab, timelineTab, playersTab, worldTab);

        // TODO: Overview
        Div overviewPage = new Div(new Paragraph("Overview content for " + campaign.getCampaignName()));
        overviewPage.setSizeFull(); // This is important for page size!!

        // TODO: Timeline
        H3 timelineTitle = new H3("Campaign Timeline");
        Div timelinePlaceholder = new Div(new Paragraph("Timeline will be here, eventually."));
        Hr hr = new Hr();

        // Events
        H3 eventGridTitle = new H3("Campaign Events");
        List<Event> campaignEvents = eventRepository.findByCampaignId(campaign.getId());

        Grid<Event> eventGrid = new Grid<>(Event.class, false);
        eventGrid.setSizeFull();
        eventGrid.getStyle().set("font-size", "var(--lumo-font-size-l)");

        eventGrid.setItems(campaignEvents);

        Button addEventButton = new Button("Add Event");

        // Event Editor
        // TODO: Show only to GMs
        H3 editEventTitle = new H3("Event Editor");

        // Event name, description, private
        TextField eventNameField = new TextField("Event Name");
        TextArea eventDescriptionField = new TextArea("Event Description");
        Checkbox privateEvent = new Checkbox("Event is private?");

        // Event Type
        RadioButtonGroup<String> eventTypeChoiceGroup = new RadioButtonGroup<>("Create new or choose existing Event Type");
        eventTypeChoiceGroup.setItems("Create new Event Type", "Choose existing Event Type");
        eventTypeChoiceGroup.setValue("Create new Event Type");

        Select<EventType> eventTypeSelect = new Select<>();
        List<EventType> userEventTypes = eventTypeRepository.findDistinctEventTypesByCampaigns(campaignRepository.findByGms(loggedUser));
        eventTypeSelect.setLabel("Select Event Type");
        eventTypeSelect.setItemLabelGenerator(EventType::getEventType);
        eventTypeSelect.setItems(userEventTypes);
        eventTypeSelect.setVisible(false);

        TextField eventTypeNameField = new TextField("Event Type Name");
        ColorPicker eventTypeColorPicker = new ColorPicker();
        eventTypeColorPicker.setLabel("Select Event Type Color");

        // Toggle create new/select event type visibility
        eventTypeChoiceGroup.addValueChangeListener(event -> {
            boolean isCreateNew = "Create New".equals(eventTypeChoiceGroup.getValue());
            eventTypeNameField.setVisible(isCreateNew);
            eventTypeColorPicker.setVisible(isCreateNew); // TODO: This disappears when changing choice?
            eventTypeSelect.setVisible(!isCreateNew);
        });

        eventTypeSelect.addValueChangeListener(event -> {
            EventType selectedEventType = eventTypeSelect.getValue();
            boolean eventTypeSelected = selectedEventType != null;
        });

        eventTypeColorPicker.addValueChangeListener(event -> {
            String eventTypeColor = event.getValue().toString();
        });

        DatePicker eventStartDateField = new DatePicker("Event Start Date");
        DatePicker eventEndDateField = new DatePicker("Event End Date. Leave blank if still ongoing.");

        // Event Place
        TextField newEventPlace = new TextField("New Event Place");
        Select<Place> eventPlaceSelect = new Select<>();
        List<Place> userPlaces = placeRepository.findPlacesByWorlds(worldRepository.findByCampaignsIn(campaignRepository.findByGms(loggedUser)));
        eventPlaceSelect.setLabel("Select Event Place");
        eventPlaceSelect.setItemLabelGenerator(Place::getPlaceName);
        eventPlaceSelect.setItems(userPlaces);
        eventPlaceSelect.setVisible(false);

        RadioButtonGroup<String> placeChoiceGroup = new RadioButtonGroup<>("Create new or choose existing Place");
        placeChoiceGroup.setItems("Create new Place", "Choose existing Place");
        placeChoiceGroup.setValue("Create new Place");

        // Toggle create new/select place visibility
        placeChoiceGroup.addValueChangeListener(event -> {
            boolean isCreateNew = "Create New".equals(placeChoiceGroup.getValue());
            newEventPlace.setVisible(isCreateNew);
            eventPlaceSelect.setVisible(!isCreateNew);
        });

        // Selected Event Type
        eventPlaceSelect.addValueChangeListener(event -> {
            Place selectedPlace = eventPlaceSelect.getValue();
            boolean eventPlaceSelected = selectedPlace != null;
        });

        // Reoccurrence type
        Select<ReoccurrenceType> reoccurrenceTypeSelect = new Select<>();
        List<ReoccurrenceType> reoccurrenceTypes = Arrays.asList(ReoccurrenceType.values());
        reoccurrenceTypeSelect.setItems(reoccurrenceTypes);
        reoccurrenceTypeSelect.setLabel("Select Reoccurrence Type");

        reoccurrenceTypeSelect.addValueChangeListener(event -> {
           ReoccurrenceType selectedReoccurrenceType = reoccurrenceTypeSelect.getValue();
           boolean reoccurrenceTypeSelected = selectedReoccurrenceType != null;
        });

        // Editor Buttons
        Button cancelEventEditButton = new Button("Cancel New Event");
        Button saveEventButton = new Button("Save Event");
        saveEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layouts
        VerticalLayout timelineRowLayout = new VerticalLayout();
        HorizontalLayout eventsHeadingLayout = new HorizontalLayout();
        VerticalLayout eventsLayout = new VerticalLayout();
        VerticalLayout newEventLayout = new VerticalLayout();
        HorizontalLayout newEventButtonLayout = new HorizontalLayout();

        // TODO: Manage event visibility based on event's private-attribute
        // Add Columns to eventGrid
        eventGrid.addColumn(Event::getName)
                .setHeader("Name")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        eventGrid.addColumn(Event::getDescription)
                .setHeader("Description")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        // TODO: Show Event Type Colour
        eventGrid.addColumn(Event::getType)
                .setHeader("Type")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        eventGrid.addColumn(Event::getPlace)
                .setHeader("Place")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        eventGrid.addColumn(Event::getReoccurring)
                .setHeader("Reoccurrence Type")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        // TODO: Get Start & End Dates from Duration
        eventGrid.addColumn(Event::getDuration)
                .setHeader("Starting date")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        eventGrid.addComponentColumn(selectedEvent -> {
            Button editButton = new Button("Edit", event -> editEvent(selectedEvent, eventsLayout, newEventLayout));
            editButton.getStyle().set("margin-right", "8px");

            Button removeButton = new Button("Remove", event -> removeEvent(selectedEvent));
            removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            return new Div(editButton, removeButton);
        }).setHeader("Actions");

        addEventButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        addEventButton.addClickListener(e -> {
            eventsLayout.setVisible(false);
            newEventLayout.setVisible(true);
            // TODO: Populate edit form if editing
        });

        cancelEventEditButton.addClickListener(e -> {
            newEventLayout.setVisible(false);
            eventsLayout.setVisible(true);
            // TODO: Clear edit form
        });

        saveEventButton.addClickListener(e -> {
            // TODO: Save new event / save modified event
            // TODO: event duration functionality
            EventDuration eventDuration = new EventDuration();

            newEventLayout.setVisible(false);
            eventsLayout.setVisible(true);
        });

        // Add Components to Layouts
        timelineRowLayout.add(timelineTitle, timelinePlaceholder, hr);
        eventsHeadingLayout.add(eventGridTitle, addEventButton);
        eventsLayout.add(eventsHeadingLayout, eventGrid);
        newEventButtonLayout.add(cancelEventEditButton, saveEventButton);
        newEventLayout.add(
                editEventTitle,
                eventNameField, eventDescriptionField,
                privateEvent,
                eventTypeChoiceGroup,
                eventTypeNameField, eventTypeColorPicker,
                eventTypeSelect,
                eventStartDateField, eventEndDateField,
                placeChoiceGroup,
                newEventPlace, eventPlaceSelect,
                reoccurrenceTypeSelect,
                newEventButtonLayout
        );

        // Set default visibility for timeline-tab
        timelineRowLayout.setVisible(true);
        eventsLayout.setVisible(true);
        newEventLayout.setVisible(false);

        timelineRowLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        eventsLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        newEventLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);

        VerticalLayout timelineLayout = new VerticalLayout();
        timelineLayout.setSizeFull();
        timelineLayout.setPadding(false);
        timelineLayout.setSpacing(true);
        timelineLayout.add(timelineRowLayout, eventsLayout, newEventLayout);

        Div timelinePage = new Div(timelineLayout);
        timelinePage.setSizeFull(); // This is important for page size!!

        // Players & GMs
        H3 playerGridTitle = new H3(campaign.getCampaignName() + "'s GMs & players");
        Grid<User> playerGrid = new Grid<>(User.class, false);
        List<User> gms = userRepository.findByGmCampaigns(campaign);
        List<User> players = userRepository.findByPlayerCampaigns(campaign);

        playerGrid.setSizeFull();
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
        // playersLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        playersLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
        playersLayout.add(playerGridTitle, playerGrid);

        Div playersPage = new Div(playersLayout);
        playersPage.setSizeFull(); // This is important for page size!!

        // TODO: World
        H3 worldTitle = new H3(campaign.getCampaignName() + "'s World");
        Div worldPlaceholder = new Div(new Paragraph("The world will eventually be here!"));
        VerticalLayout worldLayout = new VerticalLayout();
        worldLayout.add(worldTitle, worldPlaceholder);

        Div worldPage = new Div(worldLayout);
        worldPage.setSizeFull(); // This is important for page size!!

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

    private void editEvent(Event event, VerticalLayout eventsRowLayout, VerticalLayout newEventRow ) {
        eventsRowLayout.setVisible(false);
        newEventRow.setVisible(true);
        // TODO: Populate Event Edit Layout
    }

    private void removeEvent(Event event) {
        // TODO: Remove Event
    }
}