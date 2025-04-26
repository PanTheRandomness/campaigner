package com.example.application.views.campaigns;

import com.example.application.data.*;
import com.example.application.data.repositories.*;
import com.example.application.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.vaadin.addons.tatu.ColorPicker;
import org.vaadin.addons.tatu.ColorPickerVariant;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class CampaignTabsBuilder {
    private final CampaignRepository campaignRepository;
    private final EventRepository eventRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;
    private final WorldRepository worldRepository;
    private final PlaceRepository placeRepository;
    private final AreaRepository areaRepository;
    private final AuthenticatedUser authenticatedUser;

    private Event eventBeingEdited = null;

    private VerticalLayout timelineRowLayout;
    private VerticalLayout eventsLayout;
    private VerticalLayout eventForm;
    private Grid<Event> eventGrid;
    private Button addEventButton;

    private TextField eventNameField;
    private TextArea eventDescriptionField;
    private Checkbox privateEventCheckbox;
    private RadioButtonGroup<String> eventTypeChoice;
    private TextField newEventTypeName;
    private ColorPicker eventTypeColorPicker;
    private Select<EventType> eventTypeSelect;
    private TextField startDay;
    private TextField startMonth;
    private TextField startYear;
    private TextField endDay;
    private TextField endMonth;
    private TextField endYear;
    private RadioButtonGroup<String> placeChoice;
    private TextField newPlaceName;
    private TextArea newPlaceDescription;
    private TextArea newPlaceHistory;
    private Checkbox newPlacePrivate;
    private Select<Place> placeSelect;
    private RadioButtonGroup<String> areaChoice;
    private TextField newAreaName;
    private TextArea newAreaDescription;
    private TextArea newAreaHistory;
    private Checkbox newAreaPrivate;
    private Select<Area> areaSelect;
    private Select<ReoccurrenceType> reoccurrenceTypeSelect;

    public CampaignTabsBuilder(CampaignRepository campaignRepository, EventRepository eventRepository,
                               EventTypeRepository eventTypeRepository, UserRepository userRepository,
                               WorldRepository worldRepository, PlaceRepository placeRepository,
                               AreaRepository areaRepository, AuthenticatedUser authenticatedUser) {
        this.campaignRepository = campaignRepository;
        this.eventRepository = eventRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.userRepository = userRepository;
        this.worldRepository = worldRepository;
        this.placeRepository = placeRepository;
        this.areaRepository = areaRepository;
        this.authenticatedUser = authenticatedUser;
    }

    public TabsAndPages buildTabsAndPages(Campaign campaign) {
        Tabs tabs = new Tabs();
        Map<Tab, Component> tabsToPages = new LinkedHashMap<>();
        VerticalLayout pages = new VerticalLayout();
        pages.setSizeFull();

        // Create Tabs
        Tab overviewTab = new Tab(VaadinIcon.ARCHIVE.create(), new Span("Overview"));
        Tab timelineTab = new Tab(VaadinIcon.ROAD_BRANCHES.create(), new Span("Timelines"));
        Tab playersTab = new Tab(VaadinIcon.USERS.create(), new Span("Players"));
        Tab worldTab = new Tab(VaadinIcon.GLOBE.create(), new Span("World"));

        tabs.add(overviewTab, timelineTab, playersTab, worldTab);

        VerticalLayout overviewPage = createOverviewPage(campaign);
        VerticalLayout timelinePage = createTimelinePage(campaign);
        VerticalLayout playersPage = createPlayersPage(campaign);
        VerticalLayout worldPage = createWorldPage(campaign);

        pages.add(overviewPage, timelinePage, playersPage, worldPage);

        tabsToPages.put(overviewTab, overviewPage);
        tabsToPages.put(timelineTab, timelinePage);
        tabsToPages.put(playersTab, playersPage);
        tabsToPages.put(worldTab, worldPage);

        tabs.setSelectedTab(overviewTab);

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            if (selectedPage != null) {
                selectedPage.setVisible(true);
            }
        });

        tabsToPages.forEach((tab, page) -> {
            page.setVisible(tab.equals(tabs.getSelectedTab()));
        });

        return new TabsAndPages(tabs, tabsToPages, pages);
    }

    // TODO: Overview
    private VerticalLayout createOverviewPage(Campaign campaign) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        Paragraph overviewText = new Paragraph("Overview content for " + campaign.getCampaignName());
        layout.add(overviewText);

        return layout;
    }

    private VerticalLayout createTimelinePage(Campaign campaign) {
        VerticalLayout timelineLayout = new VerticalLayout();
        timelineLayout.setSizeFull();
        timelineLayout.setPadding(false);
        timelineLayout.setSpacing(false);

        // TODO: Timeline
        // Timeline placeholder
        H3 timelineTitle = new H3("Campaign Timeline");
        Paragraph placeholderParagraph = new Paragraph("Timeline will be here, eventually.");
        Hr hr = new Hr();
        timelineRowLayout = new VerticalLayout(timelineTitle, placeholderParagraph, hr);

        // Event Grid
        H3 eventGridTitle = new H3("Campaign Events");
        List<Event> campaignEvents = eventRepository.findByCampaignId(campaign.getId());
        eventGrid = new Grid<>(Event.class, false);
        eventGrid.setSizeFull();
        eventGrid.setAllRowsVisible(true);
        eventGrid.setWidthFull();
        eventGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_NO_BORDER);
        eventGrid.getStyle().set("font-size", "var(--lumo-font-size-l)");

        // TODO: Only show private events to GMs
        eventGrid.setItems(campaignEvents);

        Div emptyState = new Div(new Paragraph("No events yet. Create your first event!"));
        emptyState.getStyle().set("padding", "1em").set("text-align", "center");

        eventGrid.setEmptyStateComponent(emptyState);

        // TODO: Manage event visibility based on event's private-attribute
        eventGrid.addColumn(Event::getName).setHeader("Name").setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(Event::getDescription).setHeader("Description").setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        // TODO: Show Event Type Colour
        eventGrid.addColumn(event -> event.getType() != null ? event.getType().getEventTypeName() : "-").setHeader("Type").setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(Event::getReoccurring).setHeader("Reoccurrence Type").setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(event -> event.getPlace() != null ? event.getPlace().getPlaceName() : "-").setHeader("Place").setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(event -> event.getDuration().getStartDate().toString()).setHeader("Start Date").setAutoWidth(true);
        eventGrid.addColumn(event -> {
            CalendarDate endDate = event.getDuration().getEndDate();
            return (endDate != null) ? endDate.toString() : "-";
        }).setHeader("End Date").setAutoWidth(true);

        eventGrid.addComponentColumn(event -> {
            Button editButton = new Button("Edit");
            Button deleteButton = new Button("Delete");
            editButton.addClickListener(e -> {
                eventBeingEdited = event;
                populateEventEditorForm(event);
                timelineRowLayout.setVisible(false);
                eventsLayout.setVisible(false);
                eventForm.setVisible(true);
            });

            deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Confirm Deletion");

                VerticalLayout dialogLayout = new VerticalLayout(
                        new Paragraph("Are you sure you want to delete this event: \"" + event.getName() + "\"?")
                );
                dialogLayout.setPadding(false);
                dialogLayout.setSpacing(false);
                confirmDialog.add(dialogLayout);

                Button confirmButton = new Button("Delete", confirm -> {
                    eventRepository.delete(event);
                    eventGrid.setItems(eventRepository.findByCampaignId(campaign.getId()));
                    confirmDialog.close();
                });
                confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                Button cancelButton = new Button("Cancel", cancel -> confirmDialog.close());

                HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
                confirmDialog.getFooter().add(buttonLayout);

                confirmDialog.getElement().getStyle().set("background", "var(--lumo-base-color)");
                confirmDialog.getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-xl)");

                confirmDialog.setCloseOnOutsideClick(false);
                confirmDialog.setCloseOnEsc(true);

                confirmDialog.open();
            });

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            return actions;
        }).setHeader("Actions");

        // Add Event Button // TODO: Show only to Campaign GMs
        addEventButton = new Button("Add Event");

        // Search fields
        final EventFilters[] filters = new EventFilters[1];

        filters[0] = new EventFilters(campaign.getId(), () -> {
            List<Event> events = eventRepository.findAll(filters[0]);
            eventGrid.setItems(events);
        });

        // Title & add button
        HorizontalLayout eventHeaderLayout = new HorizontalLayout(
                eventGridTitle,
                addEventButton
        );
        eventHeaderLayout.setSizeFull();
        eventHeaderLayout.setPadding(false);
        eventHeaderLayout.setSpacing(true);

        // Grid & header
        eventsLayout = new VerticalLayout(
                eventHeaderLayout,
                eventGrid,
                filters[0]
        );

        eventForm = eventEditorForm(campaign, eventGrid, timelineRowLayout, eventsLayout);
        eventForm.setVisible(false);

        eventsLayout.setSizeFull();
        eventsLayout.setPadding(false);
        eventsLayout.setSpacing(true);

        addEventButton.addClickListener(e -> {
            timelineRowLayout.setVisible(false);
            eventsLayout.setVisible(false);
            eventForm.setVisible(true);
        });

        timelineLayout.add(
                timelineRowLayout,
                eventsLayout,
                eventForm
        );

        return timelineLayout;
    }

    private void populateEventEditorForm(Event event) {
        eventNameField.setValue(event.getName() != null ? event.getName() : "");
        eventDescriptionField.setValue(event.getDescription() != null ? event.getDescription() : "");
        privateEventCheckbox.setValue(Boolean.TRUE.equals(event.isPrivateEvent()));

        // Event Type
        if (event.getType() != null) {
            eventTypeChoice.setValue("Select Existing Type");
            eventTypeSelect.setValue(event.getType());
            newEventTypeName.clear();
            eventTypeColorPicker.setValue(event.getType().getEventColour() != null ? event.getType().getEventColour() : "#000000");
        } else {
            eventTypeChoice.setValue("Create New Type");
            newEventTypeName.clear();
            eventTypeColorPicker.setValue("#000000");
        }

        // Start date
        startDay.setValue(String.valueOf(event.getDuration().getStartDate().getDay()));
        startMonth.setValue(String.valueOf(event.getDuration().getStartDate().getMonth()));
        startYear.setValue(String.valueOf(event.getDuration().getStartDate().getYear()));

        // End date
        if (event.getDuration().getEndDate() != null) {
            endDay.setValue(String.valueOf(event.getDuration().getEndDate().getDay()));
            endMonth.setValue(String.valueOf(event.getDuration().getEndDate().getMonth()));
            endYear.setValue(String.valueOf(event.getDuration().getEndDate().getYear()));
        } else {
            endDay.clear();
            endMonth.clear();
            endYear.clear();
        }

        // Place
        if (event.getPlace() != null) {
            placeChoice.setValue("Select Existing Place");
            placeSelect.setValue(event.getPlace());
        } else {
            placeChoice.setValue("Create New Place");
        }

        // Reoccurrence
        reoccurrenceTypeSelect.setValue(event.getReoccurring());
    }

    private VerticalLayout eventEditorForm(Campaign campaign, Grid<Event> eventGrid, VerticalLayout timelineRowLayout, VerticalLayout eventsLayout) {
        Optional<User> maybeUser = authenticatedUser.get();
        User loggedUser = maybeUser.get();

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.setSizeFull();
        formLayout.setPadding(false);
        formLayout.setSpacing(false);
        formLayout.setVisible(false); // Hidden by default

        H3 formTitle = new H3("Event Editor");

        // Basic info
        eventNameField = new TextField("Event Name");
        eventDescriptionField = new TextArea("Event Description");
        privateEventCheckbox = new Checkbox("Private Event?");

        // Event Type
        eventTypeChoice = new RadioButtonGroup<>();
        eventTypeChoice.setLabel("Event Type Option");
        eventTypeChoice.setItems("Create New Type", "Select Existing Type");
        eventTypeChoice.setValue("Create New Type");

        eventTypeSelect = new Select<>();
        eventTypeSelect.setLabel("Choose Existing Event Type");
        eventTypeSelect.setItemLabelGenerator(EventType::getEventTypeName);
        eventTypeSelect.setItems(eventTypeRepository.findEventTypesByCampaign(campaign));
        eventTypeSelect.setVisible(false);

        newEventTypeName = new TextField("New Event Type Name");
        eventTypeColorPicker = new ColorPicker();
        eventTypeColorPicker.setLabel("Event Type Color");
        eventTypeColorPicker.addThemeVariants(ColorPickerVariant.COMPACT);

        // Toggle create new/select event type visibility
        eventTypeChoice.addValueChangeListener(event -> {
            boolean isCreateNew = "Create New Type".equals(eventTypeChoice.getValue());
            newEventTypeName.setVisible(isCreateNew);
            eventTypeColorPicker.setVisible(isCreateNew);
            eventTypeSelect.setVisible(!isCreateNew);
        });

        // For debugging colour choice
        eventTypeColorPicker.addValueChangeListener(event -> {
            System.out.println(eventTypeColorPicker.getValue());
        });

        // Start Date
        HorizontalLayout startDateLayout = new HorizontalLayout();
        startDay = new TextField("Start Day");
        startMonth = new TextField("Start Month");
        startYear = new TextField("Start Year");
        startDateLayout.add(startDay, startMonth, startYear);

        // End Date
        // TODO: Fix Bug: have to input an end date if something has been accidentally added in end date fields and removed
        HorizontalLayout endDateLayout = new HorizontalLayout();
        endDay = new TextField("End Day");
        endMonth = new TextField("End Month");
        endYear = new TextField("End Year");

        endDay.setTooltipText("Leave empty if event is still ongoing");
        endMonth.setTooltipText("Leave empty if event is ongoing");
        endYear.setTooltipText("Leave empty if event is ongoing");

        endDateLayout.add(endDay, endMonth, endYear);

        // Place
        placeChoice = new RadioButtonGroup<>();
        placeChoice.setLabel("Event Place Option");
        placeChoice.setItems("Create New Place", "Select Existing Place");
        placeChoice.setValue("Create New Place");

        newPlaceName = new TextField("New Place Name");
        newPlaceDescription = new TextArea("New Place Description");
        newPlaceHistory = new TextArea("New Event Place History");
        newPlacePrivate = new Checkbox("Private Place?");

        List<Place> userPlaces = placeRepository.findPlacesByWorlds(worldRepository.findByCampaignsIn(campaignRepository.findByGms(loggedUser)));

        placeSelect = new Select<>();
        placeSelect.setLabel("Choose Existing Place");
        placeSelect.setItemLabelGenerator(Place::getPlaceName);
        placeSelect.setItems(userPlaces);
        placeSelect.setVisible(false);

        // Area
        areaChoice = new RadioButtonGroup<>();
        areaChoice.setLabel("Event Area Option");
        areaChoice.setItems("Create New Area", "Select Existing Area");
        areaChoice.setValue("Create New Area");

        newAreaName = new TextField("New Area Name");
        newAreaDescription = new TextArea("New Area Description");
        newAreaHistory = new TextArea("New Area History");
        newAreaPrivate = new Checkbox("Private Area?");

        List<Area> userAreas = areaRepository.findByWorld(campaign.getCampaignWorld());

        areaSelect = new Select<>();
        areaSelect.setLabel("Select Existing Area");
        areaSelect.setItemLabelGenerator(Area::getAreaName);
        areaSelect.setItems(userAreas);
        areaSelect.setVisible(false);

        placeChoice.addValueChangeListener(event -> {
            boolean isCreatingNew = "Create New Place".equals(placeChoice.getValue());
            newPlaceName.setVisible(isCreatingNew);
            newPlaceDescription.setVisible(isCreatingNew);
            newPlaceHistory.setVisible(isCreatingNew);
            newPlacePrivate.setVisible(isCreatingNew);
            placeSelect.setVisible(!isCreatingNew);

            // Hide Area Creation if selecting existing place
            areaChoice.setVisible(isCreatingNew);
            newAreaName.setVisible(isCreatingNew);
            newAreaDescription.setVisible(isCreatingNew);
            newAreaHistory.setVisible(isCreatingNew);
            newAreaPrivate.setVisible(isCreatingNew);
            if (placeChoice.getValue().equals("Create New Place")) {
                areaSelect.setVisible(isCreatingNew); // TODO: Hide this after select place -> create place
            }
        });

        areaChoice.addValueChangeListener(event -> {
            boolean isCreatingNew = "Create New Area".equals(areaChoice.getValue());
            newAreaName.setVisible(isCreatingNew);
            newAreaDescription.setVisible(isCreatingNew);
            newAreaHistory.setVisible(isCreatingNew);
            newAreaPrivate.setVisible(isCreatingNew);
            areaSelect.setVisible(!isCreatingNew);
        });

        // Reoccurrence type
        reoccurrenceTypeSelect = new Select<>();
        reoccurrenceTypeSelect.setItems(ReoccurrenceType.values());
        reoccurrenceTypeSelect.setLabel("Reoccurrence Type");

        // Editor Buttons
        Button saveButton = new Button("Save Event");
        Button cancelButton = new Button("Cancel");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

        cancelButton.addClickListener(e -> {
            formLayout.setVisible(false);
            timelineRowLayout.setVisible(true);
            eventsLayout.setVisible(true);
            eventBeingEdited = null;
        });

        saveButton.addClickListener(e -> {
            boolean valid = true;
            System.out.println("Initiated save event...");

            // Validate name
            // TODO: Prevent Duplicate Events
            if (eventNameField.getValue().trim().isEmpty()) {
                eventNameField.setInvalid(true);
                eventNameField.setErrorMessage("Please enter a new name for event.");
                valid = false;
            } else {
                eventNameField.setInvalid(false);
            }

            // Validate Event Type
            if ("Create New Type".equals(eventTypeChoice.getValue()) && newEventTypeName.getValue().trim().isEmpty()) {
                newEventTypeName.setInvalid(true);
                newEventTypeName.setErrorMessage("Please enter a name for new event type.");
                valid = false;
            } else if ("Select Existing Type".equals(eventTypeChoice.getValue()) && eventTypeSelect.getValue() == null) {
                eventTypeSelect.setInvalid(true);
                eventTypeSelect.setErrorMessage("Please select an event type.");
                valid = false;
            } else if ("Create New Type".equals(eventTypeChoice.getValue()) && eventTypeColorPicker.getValue() == null) {
                eventTypeColorPicker.setInvalid(true);
                eventTypeColorPicker.setErrorMessage("Please select a colour for new event type."); // TODO: Fix error message not showing
                valid = false;
            } else if ("Create New Type".equals(eventTypeChoice.getValue()) && eventTypeRepository
                    .findEventTypesByCampaign(campaign)
                    .stream()
                    .anyMatch(eventType -> eventType.getEventTypeName().equalsIgnoreCase(newEventTypeName.getValue().trim()))) {
                newEventTypeName.setInvalid(true);
                newEventTypeName.setErrorMessage("This Event Type already exists in this campaign.");
                valid = false;
            } else {
                newEventTypeName.setInvalid(false);
                eventTypeColorPicker.setInvalid(false);
                eventTypeSelect.setInvalid(false);
            }

            // Validate Event Start Date
            if (startDay.getValue().trim().isEmpty() || !isNumeric(startDay.getValue())) {
                startDay.setInvalid(true);
                startDay.setErrorMessage("Please enter a valid start day for event.");
                valid = false;
            } else if (Integer.parseInt(startDay.getValue()) < 1 || Integer.parseInt(startDay.getValue()) > campaign.getCalendar().getDaysInMonth()) {
                startDay.setInvalid(true);
                startDay.setErrorMessage("The day you have selected falls outside the scope of this campaign's calendar.");
                valid = false;
            } else {
                startDay.setInvalid(false);
            }

            if (startMonth.getValue().trim().isEmpty() || !isNumeric(startMonth.getValue())) {
                startMonth.setInvalid(true);
                startMonth.setErrorMessage("Please enter a valid start month for event.");
                valid = false;
            } else if (Integer.parseInt(startMonth.getValue()) < 1 || Integer.parseInt(startMonth.getValue()) > campaign.getCalendar().getMonthsInYear()) {
                startMonth.setInvalid(true);
                startMonth.setErrorMessage("The month you have selected falls outside the scope of this campaign's calendar.");
                valid = false;
            } else {
                startMonth.setInvalid(false);
            }

            // NOTE! Event year CAN BE negative
            if (startYear.getValue().trim().isEmpty() || !isNumeric(startYear.getValue())) {
                startYear.setInvalid(true);
                startYear.setErrorMessage("Please enter a valid start year for event.");
                valid = false;
            } else {
                startYear.setInvalid(false);
            }

            // Validate Event End Date
            if (!endDay.getValue().trim().isEmpty() || !endMonth.getValue().trim().isEmpty() || !endYear.getValue().trim().isEmpty()) {
                // If end date has been given
                if (endDay.getValue().trim().isEmpty() || !isNumeric(endDay.getValue())) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage("Please enter a valid end day for event.");
                    valid = false;
                } else if (Integer.parseInt(endDay.getValue()) < 1 || Integer.parseInt(endDay.getValue()) > campaign.getCalendar().getDaysInMonth()) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage("The day you have selected falls outside the scope of this campaign's calendar.");
                    valid = false;
                } else {
                    endDay.setInvalid(false);
                }

                if (endMonth.getValue().trim().isEmpty() || !isNumeric(endMonth.getValue())) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage("Please enter a valid end month for event.");
                    valid = false;
                } else if (Integer.parseInt(endMonth.getValue()) < 1 || Integer.parseInt(endMonth.getValue()) > campaign.getCalendar().getMonthsInYear()) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage("The month you have selected falls outside the scope of this campaign's calendar.");
                    valid = false;
                } else {
                    endMonth.setInvalid(false);
                }

                // NOTE! Event year CAN BE negative
                if (endYear.getValue().trim().isEmpty() || !isNumeric(endYear.getValue())) {
                    endYear.setInvalid(true);
                    endYear.setErrorMessage("Please enter a valid end year for event.");
                    valid = false;
                } else {
                    endYear.setInvalid(false);
                }

                if (Integer.parseInt(endYear.getValue()) < Integer.parseInt(startYear.getValue())) {
                    endYear.setInvalid(true);
                    endYear.setErrorMessage("Event end year cannot end before its start year.");
                    valid = false;
                } else if (Integer.parseInt(endYear.getValue()) == Integer.parseInt(startYear.getValue())
                        && Integer.parseInt(endMonth.getValue()) < Integer.parseInt(startMonth.getValue())) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage("Event end month cannot end before its start year.");
                    valid = false;
                } else if (Integer.parseInt(endYear.getValue()) == Integer.parseInt(startYear.getValue())
                        && Integer.parseInt(endMonth.getValue()) == Integer.parseInt(startMonth.getValue())
                        && Integer.parseInt(endDay.getValue()) < Integer.parseInt(startDay.getValue())) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage("Event end day cannot end before its start day");
                    valid = false;
                } else {
                    endYear.setInvalid(false);
                    endMonth.setInvalid(false);
                    endDay.setInvalid(false);
                }
            }

            // Validate Event Place (name/existing)
            if ("Create New Place".equals(placeChoice.getValue()) && newPlaceName.getValue().trim().isEmpty()) {
                newPlaceName.setInvalid(true);
                newPlaceName.setErrorMessage("Please enter a place name.");
                valid = false;
            } else if ("Select Existing Place".equals(placeChoice.getValue()) && placeSelect.getValue() == null) {
                placeSelect.setInvalid(true);
                placeSelect.setErrorMessage("Please select a place.");
                valid = false;
            } else if ("Create New Place".equals(placeChoice.getValue()) && placeRepository
                    .findPlaceByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(place -> place.getPlaceName().equalsIgnoreCase(newPlaceName.getValue().trim()))) {
                newPlaceName.setInvalid(true);
                newPlaceName.setErrorMessage("Place already exists in this campaign's world.");
                valid = false;
            } else {
                newPlaceName.setInvalid(false);
                placeSelect.setInvalid(false);
            }

            // Validate Event Place Area (if new Place) (name/existing)
            if ("Create New Place".equals(placeChoice.getValue()) && "Create New Area".equals(areaChoice.getValue()) && newAreaName.getValue().trim().isEmpty()) {
                newAreaName.setInvalid(true);
                newAreaName.setErrorMessage("Please enter a name for area.");
                valid = false;
            } else if ("Create New Place".equals(placeChoice.getValue()) && "Select Existing Area".equals(areaChoice.getValue()) && areaSelect.getValue() == null) {
                areaSelect.setInvalid(true);
                areaSelect.setErrorMessage("Please select a place.");
                valid = false;
            } else if ("Create New Place".equals(placeChoice.getValue()) && "Create New Area".equals(areaChoice.getValue()) && areaRepository
                    .findByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(area -> area.getAreaName().equalsIgnoreCase(newAreaName.getValue().trim()))) {
                newAreaName.setInvalid(true);
                newAreaName.setErrorMessage("Area already exists in this campaign's world.");
                valid = false;
            } else {
                newAreaName.setInvalid(false);
                areaSelect.setInvalid(false);
            }

            // Validate Reoccurrence Type
            if (reoccurrenceTypeSelect.getValue() == null) {
                reoccurrenceTypeSelect.setInvalid(true);
                reoccurrenceTypeSelect.setErrorMessage("Please select a reoccurrence type.");
                valid = false;
            } else {
                reoccurrenceTypeSelect.setInvalid(false);
            }

            if (!valid) return;

            Event event;
            if (eventBeingEdited == null) {
                event = new Event();
                event.setCampaign(campaign);
            } else {
                event = eventBeingEdited;
            }

            populateEventFromForm(event, campaign);

            eventRepository.save(event);

            // Update grid
            eventGrid.setItems(eventRepository.findByCampaignId(campaign.getId()));

            // Clear form
            eventNameField.clear();
            eventDescriptionField.clear();
            privateEventCheckbox.clear();
            startDay.clear();
            startMonth.clear();
            startYear.clear();
            endDay.clear();
            endMonth.clear();
            endYear.clear();
            newEventTypeName.clear();
            eventTypeColorPicker.setValue("#000000");
            eventTypeSelect.clear();
            newPlaceName.clear();
            newPlaceDescription.clear();
            placeSelect.clear();
            newAreaName.clear();
            newAreaDescription.clear();
            areaSelect.clear();
            reoccurrenceTypeSelect.clear();

            // Reset edited event id
            eventBeingEdited = null;

            formLayout.setVisible(false);
            timelineRowLayout.setVisible(true);
            eventsLayout.setVisible(true);
        });

        formLayout.add(
                formTitle,
                eventNameField, eventDescriptionField, privateEventCheckbox,
                eventTypeChoice, newEventTypeName, eventTypeColorPicker, eventTypeSelect,
                startDateLayout, endDateLayout,
                placeChoice, newPlaceName, newPlaceDescription, newPlaceHistory, newPlacePrivate, placeSelect,
                areaChoice, newAreaName, newAreaDescription, newAreaHistory, newAreaPrivate, areaSelect,
                reoccurrenceTypeSelect,
                buttonLayout
        );

        return formLayout;
    }

    private void populateEventFromForm (Event event, Campaign campaign) {
        event.setName(eventNameField.getValue().trim());
        event.setDescription(eventDescriptionField.getValue().trim());
        event.setPrivateEvent(privateEventCheckbox.getValue());
        event.setCampaign(campaign);

        // Event Type
        if ("Create New Type".equals(eventTypeChoice.getValue())) {
            EventType newType = new EventType();
            newType.setEventTypeName(newEventTypeName.getValue().trim());
            newType.setEventColour(eventTypeColorPicker.getValue()); // TODO: Fix Bug: Now allowing black (black = null)
            eventTypeRepository.save(newType);
            event.setType(newType);
        } else {
            event.setType(eventTypeSelect.getValue());
        }

        // Area
        Area area = null;
        if ("Create New Place".equals(placeChoice.getValue()) && "Create New Area".equals(areaChoice.getValue())) {
            area = new Area();
            area.setAreaName(newAreaName.getValue().trim());
            area.setAreaDescription(newAreaDescription.getValue().trim());
            area.setAreaHistory(newAreaHistory.getValue().trim());
            area.setPrivateArea(newAreaPrivate.getValue());
            area.setWorld(campaign.getCampaignWorld());
            areaRepository.save(area);
        } else if ("Create New Place".equals(placeChoice.getValue()) && "Select Existing Area".equals(areaChoice.getValue())) {
            area = areaSelect.getValue();
        }

        // Place
        Place place;
        if ("Create New Place".equals(placeChoice.getValue())) {
            place = new Place();
            place.setPlaceName(newPlaceName.getValue().trim());
            place.setPlaceDescription(newPlaceDescription.getValue().trim());
            place.setArea(area);
            place.setPlaceHistory(newPlaceHistory.getValue().trim());
            place.setPrivatePlace(newPlacePrivate.getValue());
            placeRepository.save(place);
        } else {
            place = placeSelect.getValue();
        }
        event.setPlace(place);

        // Dates
        EventDuration duration = new EventDuration();
        duration.setStartDate(new CalendarDate(
                Integer.parseInt(startYear.getValue()),
                Integer.parseInt(startMonth.getValue()),
                Integer.parseInt(startDay.getValue())
        ));

        if (!endDay.isEmpty() && !endMonth.isEmpty() && !endYear.isEmpty()) {
            duration.setEndDate(new CalendarDate(
                    Integer.parseInt(endYear.getValue()),
                    Integer.parseInt(endMonth.getValue()),
                    Integer.parseInt(endDay.getValue())
            ));
        }

        duration.setDuration(
                duration.calculateDuration(
                        duration.getStartDate(), duration.getEndDate(), campaign.getCalendar()
                )
        );
        event.setDuration(duration);

        // Reoccurrence
        event.setReoccurring(reoccurrenceTypeSelect.getValue());

        System.out.println(event);
    }

    private VerticalLayout eventTypeEditorForm() {
        VerticalLayout layout = new VerticalLayout();
        // TODO: Add Event Type Editor
        return layout;
    }

    // TODO: Fix Grid Size!
    private VerticalLayout createPlayersPage(Campaign campaign) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        H3 title = new H3(campaign.getCampaignName() + "'s GMs & Players");

        Grid<User> playerGrid = new Grid<>(User.class, false);
        playerGrid.setSizeFull();

        List<User> gms = userRepository.findByGmCampaigns(campaign);
        List<User> players = userRepository.findByPlayerCampaigns(campaign);

        Map<User, String> roleMap = new LinkedHashMap<>();
        gms.forEach(gm -> roleMap.put(gm, "GM"));
        players.forEach(player -> roleMap.put(player, "Player"));

        playerGrid.addColumn(User::getUsername).setHeader("Username");
        playerGrid.addColumn(User::getName).setHeader("Name");
        playerGrid.addColumn(user -> roleMap.get(user)).setHeader("Role");

        playerGrid.setItems(roleMap.keySet());

        layout.add(title, playerGrid);
        return layout;
    }

    private VerticalLayout createWorldPage(Campaign campaign) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        H3 title = new H3(campaign.getCampaignName() + "'s World");
        Div placeholder = new Div(new Paragraph("The world will eventually be here!"));

        layout.add(title, placeholder);
        return layout;
    }

    public static class TabsAndPages {
        public final Tabs tabs;
        public final Map<Tab, Component> tabsToPages;
        public final VerticalLayout pages;

        public TabsAndPages(Tabs tabs, Map<Tab, Component> tabsToPages, VerticalLayout pages) {
            this.tabs = tabs;
            this.tabsToPages = tabsToPages;
            this.pages = pages;
        }
    }
}

