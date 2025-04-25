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

import java.util.*;
import java.util.List;

import org.vaadin.addons.tatu.ColorPicker;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Campaigns")
@Route("campaigns")
@Menu(order = 3, icon = LineAwesomeIconUrl.BOOK_OPEN_SOLID)
@PermitAll
public class CampaignsView extends Composite<VerticalLayout> {

    // TODO: Remove unused code
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final WorldRepository worldRepository;
    private final CalendarRepository calendarRepository;
    private final MoonRepository moonRepository;
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final EventDurationRepository eventDurationRepository;
    private final PlaceRepository placeRepository;
    private final AreaRepository areaRepository;

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
                         PlaceRepository placeRepository, AreaRepository areaRepository,
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
        this.areaRepository = areaRepository;

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

    // Here are the contents of the Overview, Timeline, Players & World Tabs
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

        // TODO: Only show private events to GMs
        eventGrid.setItems(campaignEvents);

        // TODO: Show only to Campaign GMs
        Button addEventButton = new Button("Add Event");

        // Event Editor
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
            boolean isCreateNew = "Create new Event Type".equals(eventTypeChoiceGroup.getValue());
            eventTypeNameField.setVisible(isCreateNew);
            eventTypeColorPicker.setVisible(isCreateNew);
            eventTypeSelect.setVisible(!isCreateNew);
        });

        eventTypeSelect.addValueChangeListener(event -> {
            EventType selectedEventType = eventTypeSelect.getValue();
            // boolean eventTypeSelected = selectedEventType != null;
        });

        eventTypeColorPicker.addValueChangeListener(event -> {
            String eventTypeColor = event.getValue();
        });

        // TODO: Set width
        TextField eventStartDay = new TextField("Event Start Day");
        TextField eventStartMonth = new TextField("Event Start Month");
        TextField eventStartYear = new TextField("Event Start Year");

        TextField eventEndDay = new TextField("Event End Day");
        TextField eventEndMonth = new TextField("Event End Month");
        TextField eventEndYear = new TextField("Event End Year");

        eventEndDay.setTooltipText("Leave empty if event is still ongoing");
        eventEndMonth.setTooltipText("Leave empty if event is ongoing");
        eventEndYear.setTooltipText("Leave empty if event is ongoing");

        // Event Place & Area
        TextField newEventPlaceName = new TextField("New Event Place Name");
        TextArea newEventPlaceDescription = new TextArea("New Event Description");
        TextArea newEventPlaceHistory = new TextArea("New Event Place History");
        Checkbox newEventPlacePrivate = new Checkbox("Private Place?");

        TextField newEventPlaceAreaName = new TextField("New Area Name");
        TextArea newEventPlaceAreaDescription = new TextArea("New Area Description");
        TextArea newEventPlaceAreaHistory = new TextArea("New Area History");
        Checkbox newEventPlaceAreaPrivate = new Checkbox("Private Area?");

        Select<Place> newEventPlaceSelect = new Select<>();
        Select<Area> newEventPlaceAreaSelect = new Select<>();

        List<Place> userPlaces = placeRepository.findPlacesByWorlds(worldRepository.findByCampaignsIn(campaignRepository.findByGms(loggedUser)));
        List<Area> userAreas = areaRepository.findByWorld(campaign.getCampaignWorld());

        // Place Select configuration
        newEventPlaceSelect.setLabel("Select Event Place");
        newEventPlaceSelect.setItemLabelGenerator(Place::getPlaceName);
        newEventPlaceSelect.setItems(userPlaces);
        newEventPlaceSelect.setVisible(false);

        // Area Select Configuration
        newEventPlaceAreaSelect.setLabel("Select place's Area");
        newEventPlaceAreaSelect.setItemLabelGenerator(Area::getAreaName);
        newEventPlaceAreaSelect.setItems(userAreas);
        newEventPlaceAreaSelect.setVisible(false);

        RadioButtonGroup<String> eventPlaceChoiceGroup = new RadioButtonGroup<>("Create new or choose existing Place");
        eventPlaceChoiceGroup.setItems("Create new Place", "Choose existing Place");
        eventPlaceChoiceGroup.setValue("Create new Place");

        RadioButtonGroup<String> eventPlaceAreaChoiceGroup = new RadioButtonGroup<>("Create new or choose existing Area");
        eventPlaceAreaChoiceGroup.setItems("Create new Area", "Choose existing Area");
        eventPlaceAreaChoiceGroup.setValue("Create new Area");

        // Toggle create new/select place visibility
        eventPlaceChoiceGroup.addValueChangeListener(event -> {
            boolean isCreateNew = "Create new Place".equals(eventPlaceChoiceGroup.getValue());
            newEventPlaceName.setVisible(isCreateNew);
            newEventPlaceDescription.setVisible(isCreateNew);
            newEventPlaceHistory.setVisible(isCreateNew);
            newEventPlacePrivate.setVisible(isCreateNew);
            newEventPlaceSelect.setVisible(!isCreateNew);
        });

        // Selected Event Place
        newEventPlaceSelect.addValueChangeListener(event -> {
            Place selectedPlace = newEventPlaceSelect.getValue();
            // boolean eventPlaceSelected = selectedPlace != null;
        });

        // Toggle create new/select area visibility
        eventPlaceAreaChoiceGroup.addValueChangeListener(event -> {
            boolean isCreateNew = "Create new Area".equals(eventPlaceAreaChoiceGroup.getValue());
            newEventPlaceAreaName.setVisible(isCreateNew);
            newEventPlaceAreaDescription.setVisible(isCreateNew);
            newEventPlaceAreaHistory.setVisible(isCreateNew);
            newEventPlaceAreaPrivate.setVisible(isCreateNew);
            newEventPlaceAreaSelect.setVisible(!isCreateNew);
        });

        // Selected Event Place Area
        newEventPlaceAreaSelect.addValueChangeListener(event -> {
            Area selectedArea = newEventPlaceAreaSelect.getValue();
            // boolean eventPlaceAreaSelected = selectedArea != null;
        });

        // Reoccurrence type
        Select<ReoccurrenceType> eventReoccurrenceTypeSelect = new Select<>();
        List<ReoccurrenceType> reoccurrenceTypes = Arrays.asList(ReoccurrenceType.values());
        eventReoccurrenceTypeSelect.setItems(reoccurrenceTypes);
        eventReoccurrenceTypeSelect.setLabel("Select Reoccurrence Type");

        eventReoccurrenceTypeSelect.addValueChangeListener(event -> {
           ReoccurrenceType selectedReoccurrenceType = eventReoccurrenceTypeSelect.getValue();
           // boolean reoccurrenceTypeSelected = selectedReoccurrenceType != null;
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
            // Validation
            boolean eventIsValid = true;

            // Validate name
            if (eventNameField.getValue().trim().isEmpty()) {
                eventNameField.setInvalid(true);
                eventNameField.setErrorMessage("Please enter a new name for event.");
                eventIsValid = false;
            } else {
                eventNameField.setInvalid(false);
            }

            // Validate Event Type
            if ("Create new Event Type".equals(eventTypeChoiceGroup.getValue()) && eventTypeNameField.getValue().trim().isEmpty()) {
                eventTypeNameField.setInvalid(true);
                eventTypeNameField.setErrorMessage("Please enter a name for new event type.");
                eventIsValid = false;
            } else if ("Choose existing Event Type".equals(eventTypeChoiceGroup.getValue()) && eventTypeSelect.getValue() == null) {
                eventTypeSelect.setInvalid(true);
                eventTypeSelect.setErrorMessage("Please select an event type.");
                eventIsValid = false;
            } else {
                eventTypeNameField.setInvalid(false);
                eventTypeSelect.setInvalid(false);
            }

            // NOTE: Can be made more efficient with local variables (?)
            // Validate Event Start Date
            if(eventStartDay.getValue().trim().isEmpty() || !isNumeric(eventStartDay.getValue())) {
                eventStartDay.setInvalid(true);
                eventStartDay.setErrorMessage("Please enter a valid start day for event.");
                eventIsValid = false;
            } else if (Integer.parseInt(eventStartDay.getValue()) < 1 || Integer.parseInt(eventStartDay.getValue()) > campaign.getCalendar().getDaysInMonth()){
                eventStartDay.setInvalid(true);
                eventStartDay.setErrorMessage("The day you have selected falls outside the scope of this campaign's calendar.");
                eventIsValid = false;
            } else {
                eventStartDay.setInvalid(false);
            }

            if(eventStartMonth.getValue().trim().isEmpty() || !isNumeric(eventStartMonth.getValue())) {
                eventStartMonth.setInvalid(true);
                eventStartMonth.setErrorMessage("Please enter a valid start month for event.");
                eventIsValid = false;
            } else if (Integer.parseInt(eventStartMonth.getValue()) < 1 || Integer.parseInt(eventStartMonth.getValue()) > campaign.getCalendar().getMonthsInYear()){
                eventStartMonth.setInvalid(true);
                eventStartMonth.setErrorMessage("The month you have selected falls outside the scope of this campaign's calendar.");
                eventIsValid = false;
            } else {
                eventStartMonth.setInvalid(false);
            }

            // NOTE! Event year CAN BE negative
            if(eventStartYear.getValue().trim().isEmpty() || !isNumeric(eventStartYear.getValue()) ) {
                eventStartYear.setInvalid(true);
                eventStartYear.setErrorMessage("Please enter a valid start year for event.");
                eventIsValid = false;
            } else {
                eventStartYear.setInvalid(false);
            }

            // Validate Event End Date
            if (!eventEndDay.getValue().trim().isEmpty() || !eventEndMonth.getValue().trim().isEmpty() || !eventEndYear.getValue().trim().isEmpty()) {
                // If end date has been given
                if (eventEndDay.getValue().trim().isEmpty() || !isNumeric(eventEndDay.getValue())) {
                    eventEndDay.setInvalid(true);
                    eventEndDay.setErrorMessage("Please enter a valid end day for event.");
                    eventIsValid = false;
                } else if (Integer.parseInt(eventEndDay.getValue()) < 1 || Integer.parseInt(eventEndDay.getValue()) > campaign.getCalendar().getDaysInMonth()){
                    eventEndDay.setInvalid(true);
                    eventEndDay.setErrorMessage("The day you have selected falls outside the scope of this campaign's calendar.");
                    eventIsValid = false;
                } else {
                    eventEndDay.setInvalid(false);
                }

                if(eventEndMonth.getValue().trim().isEmpty() || !isNumeric(eventEndMonth.getValue())) {
                    eventEndMonth.setInvalid(true);
                    eventEndMonth.setErrorMessage("Please enter a valid end month for event.");
                    eventIsValid = false;
                } else if (Integer.parseInt(eventEndMonth.getValue()) < 1 || Integer.parseInt(eventEndMonth.getValue()) > campaign.getCalendar().getMonthsInYear()){
                    eventEndMonth.setInvalid(true);
                    eventEndMonth.setErrorMessage("The month you have selected falls outside the scope of this campaign's calendar.");
                    eventIsValid = false;
                } else {
                    eventEndMonth.setInvalid(false);
                }

                // NOTE! Event year CAN BE negative
                if(eventEndYear.getValue().trim().isEmpty() || !isNumeric(eventEndYear.getValue()) ) {
                    eventEndYear.setInvalid(true);
                    eventEndYear.setErrorMessage("Please enter a valid end year for event.");
                    eventIsValid = false;
                } else {
                    eventEndYear.setInvalid(false);
                }

                int startYear = Integer.parseInt(eventStartYear.getValue().trim());
                int startMonth = Integer.parseInt(eventStartMonth.getValue().trim());
                int startDay = Integer.parseInt(eventStartDay.getValue().trim());
                int endYear = Integer.parseInt(eventEndYear.getValue().trim());
                int endMonth = Integer.parseInt(eventEndMonth.getValue().trim());
                int endDay = Integer.parseInt(eventEndDay.getValue().trim());

                // End date cannot be before start date
                if (endYear < startYear) {
                    eventEndYear.setInvalid(true);
                    eventEndYear.setErrorMessage("Event end year cannot end before its start year.");
                    eventIsValid = false;
                } else if (endYear == startYear && endMonth < startMonth) {
                    eventEndMonth.setInvalid(true);
                    eventEndMonth.setErrorMessage("Event end month cannot end before its start year.");
                    eventIsValid = false;
                } else if (endYear == startYear && endMonth == startMonth && endDay < startDay) {
                    eventEndDay.setInvalid(true);
                    eventEndDay.setErrorMessage("Event end day cannot end before its start day");
                    eventIsValid = false;
                } else {
                    eventEndYear.setInvalid(false);
                    eventEndMonth.setInvalid(false);
                    eventEndDay.setInvalid(false);
                }
            }

            // Validate Event Place (name/existing)
            if ("Create new Place".equals(eventPlaceChoiceGroup.getValue()) && newEventPlaceName.getValue().trim().isEmpty()) {
                newEventPlaceName.setInvalid(true);
                newEventPlaceName.setErrorMessage("Please enter a place name.");
                eventIsValid = false;
            } else if ("Choose existing Place".equals(eventPlaceChoiceGroup.getValue()) && newEventPlaceSelect.getValue() == null) {
                newEventPlaceSelect.setInvalid(true);
                newEventPlaceSelect.setErrorMessage("Please select a place.");
                eventIsValid = false;
            } else if ("Create new Place".equals(eventPlaceChoiceGroup.getValue()) && placeRepository
                    .findPlaceByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(place -> place.getPlaceName().equalsIgnoreCase(newEventPlaceName.getValue().trim()))) {
                newEventPlaceName.setInvalid(true);
                newEventPlaceName.setErrorMessage("Place already exists in this campaign's world.");
                eventIsValid = false;
            } else {
                newEventPlaceName.setInvalid(false);
                newEventPlaceSelect.setInvalid(false);
            }

            // Validate Event Place Area (if new Place) (name/existing)
            if ("Create new Area".equals(eventPlaceAreaChoiceGroup.getValue()) && newEventPlaceAreaName.getValue().trim().isEmpty()) {
                newEventPlaceAreaName.setInvalid(true);
                newEventPlaceAreaName.setErrorMessage("Please enter a name for area.");
                eventIsValid = false;
            } else if ("Choose existing Area".equals(eventPlaceAreaChoiceGroup.getValue()) && newEventPlaceAreaSelect.getValue() == null) {
                newEventPlaceAreaSelect.setInvalid(true);
                newEventPlaceAreaSelect.setErrorMessage("Please select a place.");
                eventIsValid = false;
            } else if ("Create new Area".equals(eventPlaceAreaChoiceGroup.getValue()) && areaRepository
                    .findByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(area -> area.getAreaName().equalsIgnoreCase(newEventPlaceAreaName.getValue().trim()))) {
                newEventPlaceAreaName.setInvalid(true);
                newEventPlaceAreaName.setErrorMessage("Area already exists in this campaign's world.");
                eventIsValid = false;
            } else {
                newEventPlaceAreaName.setInvalid(false);
                newEventPlaceAreaSelect.setInvalid(false);
            }

            // Validate Reoccurrence Type
            if (eventReoccurrenceTypeSelect.getValue() == null) {
                eventReoccurrenceTypeSelect.setInvalid(true);
                eventReoccurrenceTypeSelect.setErrorMessage("Please select a reoccurrence type.");
                eventIsValid = false;
            } else {
                eventReoccurrenceTypeSelect.setInvalid(false);
            }

            if (eventIsValid) {
                // TODO: Handle Event Type Save if new event type
                // TODO: Handle Event Duration Save

                EventDuration eventDuration = new EventDuration();
                // TODO: Handle Event Area Save if new area (link to world)
                // TODO Handle Event Place Save if new place (link to area)
                // TODO: Create event and Handle save
                // TODO: Modified event save handling
//                Event editedEvent = new Event(); // TODO: Fix Write-Only
//                editedEvent.setName(newEventPlaceName.getValue().trim());
//                editedEvent.setDescription(newEventPlaceDescription.getValue().trim());

                // Finally change back to eventGrid view
                newEventLayout.setVisible(false);
                eventsLayout.setVisible(true);
            }

            /* Things to save
            * - Description
            * - Private
            * - Event Type: new (type*, colour* => create) or existing*
            * - Event Duration: Start date* & End date, calculate duration*
            * - Event Place: new (name*, description, area!, history, private) or existing*
            * - Event Place Area: new (name*, description, history, world (current campaign world), private) or existing*
            * - Reoccurrence Type*
            * */
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
                eventStartDay, eventStartMonth, eventStartYear,
                eventEndDay, eventEndMonth, eventEndYear,
                eventPlaceChoiceGroup,
                newEventPlaceName, newEventPlaceDescription,
                newEventPlaceHistory, newEventPlacePrivate,
                newEventPlaceSelect,
                eventPlaceAreaChoiceGroup,
                newEventPlaceAreaName, newEventPlaceAreaDescription,
                newEventPlaceAreaHistory, newEventPlaceAreaPrivate,
                newEventPlaceAreaSelect,
                eventReoccurrenceTypeSelect,
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

        playerGrid.addColumn(characterMap::get)
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
        /*Set<Campaign> campaigns = new HashSet<>();
        campaigns.addAll(user.getGmCampaigns());
        campaigns.addAll(user.getPlayerCampaigns());*/

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

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}