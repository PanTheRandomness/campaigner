package com.example.application.views.campaigns;

import com.example.application.data.*;
import com.example.application.data.repositories.*;
import com.example.application.security.AuthenticatedUser;
import com.example.application.util.UIRegistryWithGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
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
        Tab overviewTab = new Tab(VaadinIcon.ARCHIVE.create(), new Span(UI.getCurrent().getTranslation("overview")));
        Tab timelineTab = new Tab(VaadinIcon.ROAD_BRANCHES.create(), new Span(UI.getCurrent().getTranslation("timelines")));
        Tab playersTab = new Tab(VaadinIcon.USERS.create(), new Span(UI.getCurrent().getTranslation("players")));
        Tab worldTab = new Tab(VaadinIcon.GLOBE.create(), new Span(UI.getCurrent().getTranslation("world")));

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

        Paragraph overviewText = new Paragraph(UI.getCurrent().getTranslation("overview.text") + " " + campaign.getCampaignName());
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
        H3 timelineTitle = new H3(UI.getCurrent().getTranslation("campaign.timeline.title"));
        Paragraph placeholderParagraph = new Paragraph(UI.getCurrent().getTranslation("campaign.timeline.placeholder"));
        Hr hr = new Hr();
        timelineRowLayout = new VerticalLayout(timelineTitle, placeholderParagraph, hr);

        // Event Grid
        H3 eventGridTitle = new H3(UI.getCurrent().getTranslation("campaign.timeline.events"));
        List<Event> campaignEvents = eventRepository.findByCampaignId(campaign.getId());
        eventGrid = new Grid<>(Event.class, false);
        eventGrid.setSizeFull();
        eventGrid.setAllRowsVisible(true);
        eventGrid.setWidthFull();
        eventGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_NO_BORDER);
        eventGrid.getStyle().set("font-size", "var(--lumo-font-size-l)");

        // TODO: Only show private events to GMs
        eventGrid.setItems(campaignEvents);

        Div emptyState = new Div(new Paragraph(UI.getCurrent().getTranslation("campaign.timeline.empty")));
        emptyState.getStyle().set("padding", "1em").set("text-align", "center");

        eventGrid.setEmptyStateComponent(emptyState);

        // TODO: Manage event visibility based on event's private-attribute
        eventGrid.addColumn(Event::getName).setHeader(UI.getCurrent().getTranslation("name")).setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(Event::getDescription).setHeader(UI.getCurrent().getTranslation("description")).setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        // TODO: Show Event Type Colour
        eventGrid.addColumn(event -> event.getType() != null ? event.getType().getEventTypeName() : "-").setHeader(UI.getCurrent().getTranslation("type")).setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(Event::getReoccurring).setHeader(UI.getCurrent().getTranslation("reoccurrence")).setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(event -> event.getPlace() != null ? event.getPlace().getPlaceName() : "-").setHeader(UI.getCurrent().getTranslation("place")).setAutoWidth(true).setTextAlign(ColumnTextAlign.START);
        eventGrid.addColumn(event -> event.getDuration().getStartDate().toString()).setHeader(UI.getCurrent().getTranslation("start_date")).setAutoWidth(true);
        eventGrid.addColumn(event -> {
            CalendarDate endDate = event.getDuration().getEndDate();
            return (endDate != null) ? endDate.toString() : "-";
        }).setHeader(UI.getCurrent().getTranslation("end_date")).setAutoWidth(true);

        eventGrid.addComponentColumn(event -> {
            Button editButton = new Button(UI.getCurrent().getTranslation("edit"));
            Button deleteButton = new Button(UI.getCurrent().getTranslation("delete"));
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
                confirmDialog.setHeaderTitle(UI.getCurrent().getTranslation("confirm_delete"));

                VerticalLayout dialogLayout = new VerticalLayout(
                        new Paragraph(UI.getCurrent().getTranslation("confirm_delete.text") + "\"" + event.getName() + "\"?")
                );
                dialogLayout.setPadding(false);
                dialogLayout.setSpacing(false);
                confirmDialog.add(dialogLayout);

                Button confirmButton = new Button(UI.getCurrent().getTranslation("delete"), confirm -> {
                    eventRepository.delete(event);
                    eventGrid.setItems(eventRepository.findByCampaignId(campaign.getId()));
                    confirmDialog.close();
                });
                confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

                Button cancelButton = new Button(UI.getCurrent().getTranslation("cancel"), cancel -> confirmDialog.close());

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
        }).setHeader(UI.getCurrent().getTranslation("actions"));

        // Add Event Button // TODO: Show only to Campaign GMs
        addEventButton = new Button(UI.getCurrent().getTranslation("add_event"));

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
        UIRegistryWithGrid.register(UI.getCurrent(), eventGrid, campaign);
        return timelineLayout;
    }

    private void populateEventEditorForm(Event event) {
        eventNameField.setValue(event.getName() != null ? event.getName() : "");
        eventDescriptionField.setValue(event.getDescription() != null ? event.getDescription() : "");
        privateEventCheckbox.setValue(Boolean.TRUE.equals(event.isPrivateEvent()));

        // Event Type
        if (event.getType() != null) {
            eventTypeChoice.setValue(UI.getCurrent().getTranslation("select_existing_type"));
            eventTypeSelect.setValue(event.getType());
            newEventTypeName.clear();
            eventTypeColorPicker.setValue(event.getType().getEventColour() != null ? event.getType().getEventColour() : "#000000");
        } else {
            eventTypeChoice.setValue(UI.getCurrent().getTranslation("create_new_type"));
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
            placeChoice.setValue(UI.getCurrent().getTranslation("select_existing_place"));
            placeSelect.setValue(event.getPlace());
        } else {
            placeChoice.setValue(UI.getCurrent().getTranslation("create_new_place"));
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

        H3 formTitle = new H3(UI.getCurrent().getTranslation("event_editor"));

        // Basic info
        eventNameField = new TextField(UI.getCurrent().getTranslation("event.name"));
        eventDescriptionField = new TextArea(UI.getCurrent().getTranslation("event.description"));
        privateEventCheckbox = new Checkbox(UI.getCurrent().getTranslation("event.private"));

        // Event Type
        eventTypeChoice = new RadioButtonGroup<>();
        eventTypeChoice.setLabel(UI.getCurrent().getTranslation("event_type"));
        eventTypeChoice.setItems(UI.getCurrent().getTranslation("create_new_type"), UI.getCurrent().getTranslation("select_existing_type"));
        eventTypeChoice.setValue(UI.getCurrent().getTranslation("create_new_type"));

        eventTypeSelect = new Select<>();
        eventTypeSelect.setLabel(UI.getCurrent().getTranslation("select_existing_event_type"));
        eventTypeSelect.setItemLabelGenerator(EventType::getEventTypeName);
        eventTypeSelect.setItems(eventTypeRepository.findEventTypesByCampaign(campaign));
        eventTypeSelect.setVisible(false);

        newEventTypeName = new TextField(UI.getCurrent().getTranslation("new_event_type_name"));
        eventTypeColorPicker = new ColorPicker();
        eventTypeColorPicker.setLabel(UI.getCurrent().getTranslation("event_type_colour"));
        eventTypeColorPicker.addThemeVariants(ColorPickerVariant.COMPACT);

        // Toggle create new/select event type visibility
        eventTypeChoice.addValueChangeListener(event -> {
            boolean isCreateNew = UI.getCurrent().getTranslation("create_new_type").equals(eventTypeChoice.getValue());
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
        startDay = new TextField(UI.getCurrent().getTranslation("start_day"));
        startMonth = new TextField(UI.getCurrent().getTranslation("start_month"));
        startYear = new TextField(UI.getCurrent().getTranslation("start_year"));
        startDateLayout.add(startDay, startMonth, startYear);

        // End Date
        // TODO: Fix Bug: have to input an end date if something has been accidentally added in end date fields and removed
        HorizontalLayout endDateLayout = new HorizontalLayout();
        endDay = new TextField(UI.getCurrent().getTranslation("end_day"));
        endMonth = new TextField(UI.getCurrent().getTranslation("end_month"));
        endYear = new TextField(UI.getCurrent().getTranslation("end_year"));

        endDay.setTooltipText(UI.getCurrent().getTranslation("end.leave_empty"));
        endMonth.setTooltipText(UI.getCurrent().getTranslation("end.leave_empty"));
        endYear.setTooltipText(UI.getCurrent().getTranslation("end.leave_empty"));

        endDateLayout.add(endDay, endMonth, endYear);

        // Place
        placeChoice = new RadioButtonGroup<>();
        placeChoice.setLabel(UI.getCurrent().getTranslation("event.place"));
        placeChoice.setItems(UI.getCurrent().getTranslation("create_new_place"), UI.getCurrent().getTranslation("select_existing_place"));
        placeChoice.setValue(UI.getCurrent().getTranslation("create_new_place"));

        newPlaceName = new TextField(UI.getCurrent().getTranslation("place.name"));
        newPlaceDescription = new TextArea(UI.getCurrent().getTranslation("place.description"));
        newPlaceHistory = new TextArea(UI.getCurrent().getTranslation("place.history"));
        newPlacePrivate = new Checkbox(UI.getCurrent().getTranslation("place.private"));

        List<Place> userPlaces = placeRepository.findPlacesByWorlds(worldRepository.findByCampaignsIn(campaignRepository.findByGms(loggedUser)));

        placeSelect = new Select<>();
        placeSelect.setLabel(UI.getCurrent().getTranslation("select_existing_place"));
        placeSelect.setItemLabelGenerator(Place::getPlaceName);
        placeSelect.setItems(userPlaces);
        placeSelect.setVisible(false);

        // Area
        // TODO: Fix Bug: Localization un-sets radio button group value
        areaChoice = new RadioButtonGroup<>();
        areaChoice.setLabel(UI.getCurrent().getTranslation("event_area"));
        areaChoice.setItems(UI.getCurrent().getTranslation("create_new_area"), UI.getCurrent().getTranslation("select_existing_area"));
        areaChoice.setValue(UI.getCurrent().getTranslation("event_area"));

        newAreaName = new TextField(UI.getCurrent().getTranslation("area.name"));
        newAreaDescription = new TextArea(UI.getCurrent().getTranslation("area.description"));
        newAreaHistory = new TextArea(UI.getCurrent().getTranslation("area.history"));
        newAreaPrivate = new Checkbox(UI.getCurrent().getTranslation("area.private"));

        List<Area> userAreas = areaRepository.findByWorld(campaign.getCampaignWorld());

        areaSelect = new Select<>();
        areaSelect.setLabel(UI.getCurrent().getTranslation("select_existing_area"));
        areaSelect.setItemLabelGenerator(Area::getAreaName);
        areaSelect.setItems(userAreas);
        areaSelect.setVisible(false);

        placeChoice.addValueChangeListener(event -> {
            boolean isCreatingNew = UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue());
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
            if (placeChoice.getValue().equals(UI.getCurrent().getTranslation("create_new_place"))) {
                areaSelect.setVisible(isCreatingNew); // TODO: Hide this after select place -> create place
            }
        });

        areaChoice.addValueChangeListener(event -> {
            boolean isCreatingNew = UI.getCurrent().getTranslation("event_area").equals(areaChoice.getValue());
            newAreaName.setVisible(isCreatingNew);
            newAreaDescription.setVisible(isCreatingNew);
            newAreaHistory.setVisible(isCreatingNew);
            newAreaPrivate.setVisible(isCreatingNew);
            areaSelect.setVisible(!isCreatingNew);
        });

        // Reoccurrence type
        reoccurrenceTypeSelect = new Select<>();
        reoccurrenceTypeSelect.setItems(ReoccurrenceType.values());
        // TODO: Bug Fix: Label Localization gets error 500
        // reoccurrenceTypeSelect.setItemLabelGenerator(type -> UI.getCurrent().getTranslation("reoccurrence." + type.name().toLowerCase()));
        reoccurrenceTypeSelect.setLabel(UI.getCurrent().getTranslation("reoccurrence"));

        // Editor Buttons
        Button saveButton = new Button(UI.getCurrent().getTranslation("save_event"));
        Button cancelButton = new Button(UI.getCurrent().getTranslation("cancel"));
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
            // System.out.println("Initiated save event...");

            // Validate name
            // TODO: Prevent Duplicate Events
            if (eventNameField.getValue().trim().isEmpty()) {
                eventNameField.setInvalid(true);
                eventNameField.setErrorMessage(UI.getCurrent().getTranslation("event.missing_name"));
                valid = false;
            } else {
                eventNameField.setInvalid(false);
            }

            // Validate Event Type
            if (UI.getCurrent().getTranslation("create_new_type").equals(eventTypeChoice.getValue()) && newEventTypeName.getValue().trim().isEmpty()) {
                newEventTypeName.setInvalid(true);
                newEventTypeName.setErrorMessage(UI.getCurrent().getTranslation("event.missing_event_type_name"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("select_existing_type").equals(eventTypeChoice.getValue()) && eventTypeSelect.getValue() == null) {
                eventTypeSelect.setInvalid(true);
                eventTypeSelect.setErrorMessage(UI.getCurrent().getTranslation("event.missing_event_type"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("create_new_type").equals(eventTypeChoice.getValue()) && eventTypeColorPicker.getValue() == null) {
                eventTypeColorPicker.setInvalid(true);
                eventTypeColorPicker.setErrorMessage(UI.getCurrent().getTranslation("event.missing_event_type_colour")); // TODO: Fix error message not showing
                valid = false;
            } else if (UI.getCurrent().getTranslation("create_new_type").equals(eventTypeChoice.getValue()) && eventTypeRepository
                    .findEventTypesByCampaign(campaign)
                    .stream()
                    .anyMatch(eventType -> eventType.getEventTypeName().equalsIgnoreCase(newEventTypeName.getValue().trim()))) {
                newEventTypeName.setInvalid(true);
                newEventTypeName.setErrorMessage(UI.getCurrent().getTranslation("event.event_type_already_exists"));
                valid = false;
            } else {
                newEventTypeName.setInvalid(false);
                eventTypeColorPicker.setInvalid(false);
                eventTypeSelect.setInvalid(false);
            }

            // Validate Event Start Date
            if (startDay.getValue().trim().isEmpty() || !isNumeric(startDay.getValue())) {
                startDay.setInvalid(true);
                startDay.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_start_day"));
                valid = false;
            } else if (Integer.parseInt(startDay.getValue()) < 1 || Integer.parseInt(startDay.getValue()) > campaign.getCalendar().getDaysInMonth()) {
                startDay.setInvalid(true);
                startDay.setErrorMessage(UI.getCurrent().getTranslation("event.day_overflow"));
                valid = false;
            } else {
                startDay.setInvalid(false);
            }

            if (startMonth.getValue().trim().isEmpty() || !isNumeric(startMonth.getValue())) {
                startMonth.setInvalid(true);
                startMonth.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_start_month"));
                valid = false;
            } else if (Integer.parseInt(startMonth.getValue()) < 1 || Integer.parseInt(startMonth.getValue()) > campaign.getCalendar().getMonthsInYear()) {
                startMonth.setInvalid(true);
                startMonth.setErrorMessage(UI.getCurrent().getTranslation("event.month_overflow"));
                valid = false;
            } else {
                startMonth.setInvalid(false);
            }

            // NOTE! Event year CAN BE negative
            if (startYear.getValue().trim().isEmpty() || !isNumeric(startYear.getValue())) {
                startYear.setInvalid(true);
                startYear.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_start_year"));
                valid = false;
            } else {
                startYear.setInvalid(false);
            }

            // Validate Event End Date
            if (!endDay.getValue().trim().isEmpty() || !endMonth.getValue().trim().isEmpty() || !endYear.getValue().trim().isEmpty()) {
                // If end date has been given
                if (endDay.getValue().trim().isEmpty() || !isNumeric(endDay.getValue())) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_end_day"));
                    valid = false;
                } else if (Integer.parseInt(endDay.getValue()) < 1 || Integer.parseInt(endDay.getValue()) > campaign.getCalendar().getDaysInMonth()) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage(UI.getCurrent().getTranslation("event.day_overflow"));
                    valid = false;
                } else {
                    endDay.setInvalid(false);
                }

                if (endMonth.getValue().trim().isEmpty() || !isNumeric(endMonth.getValue())) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_end_month"));
                    valid = false;
                } else if (Integer.parseInt(endMonth.getValue()) < 1 || Integer.parseInt(endMonth.getValue()) > campaign.getCalendar().getMonthsInYear()) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage(UI.getCurrent().getTranslation("event.month_overflow"));
                    valid = false;
                } else {
                    endMonth.setInvalid(false);
                }

                // NOTE! Event year CAN BE negative
                if (endYear.getValue().trim().isEmpty() || !isNumeric(endYear.getValue())) {
                    endYear.setInvalid(true);
                    endYear.setErrorMessage(UI.getCurrent().getTranslation("event.invalid_end_year"));
                    valid = false;
                } else {
                    endYear.setInvalid(false);
                }

                if (Integer.parseInt(endYear.getValue()) < Integer.parseInt(startYear.getValue())) {
                    endYear.setInvalid(true);
                    endYear.setErrorMessage(UI.getCurrent().getTranslation("event.end_year_before_start"));
                    valid = false;
                } else if (Integer.parseInt(endYear.getValue()) == Integer.parseInt(startYear.getValue())
                        && Integer.parseInt(endMonth.getValue()) < Integer.parseInt(startMonth.getValue())) {
                    endMonth.setInvalid(true);
                    endMonth.setErrorMessage(UI.getCurrent().getTranslation("event.end_month_before_start"));
                    valid = false;
                } else if (Integer.parseInt(endYear.getValue()) == Integer.parseInt(startYear.getValue())
                        && Integer.parseInt(endMonth.getValue()) == Integer.parseInt(startMonth.getValue())
                        && Integer.parseInt(endDay.getValue()) < Integer.parseInt(startDay.getValue())) {
                    endDay.setInvalid(true);
                    endDay.setErrorMessage(UI.getCurrent().getTranslation("event.end_day_before_start"));
                    valid = false;
                } else {
                    endYear.setInvalid(false);
                    endMonth.setInvalid(false);
                    endDay.setInvalid(false);
                }
            }

            // Validate Event Place (name/existing)
        if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && newPlaceName.getValue().trim().isEmpty()) {
                newPlaceName.setInvalid(true);
                newPlaceName.setErrorMessage(UI.getCurrent().getTranslation("event.missing_place_name"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("select_existing_place").equals(placeChoice.getValue()) && placeSelect.getValue() == null) {
                placeSelect.setInvalid(true);
                placeSelect.setErrorMessage(UI.getCurrent().getTranslation("event.missing_place"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && placeRepository
                    .findPlaceByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(place -> place.getPlaceName().equalsIgnoreCase(newPlaceName.getValue().trim()))) {
                newPlaceName.setInvalid(true);
                newPlaceName.setErrorMessage(UI.getCurrent().getTranslation("event.place_already_exists"));
                valid = false;
            } else {
                newPlaceName.setInvalid(false);
                placeSelect.setInvalid(false);
            }

            // Validate Event Place Area (if new Place) (name/existing)
            if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && UI.getCurrent().getTranslation("create_new_area").equals(areaChoice.getValue()) && newAreaName.getValue().trim().isEmpty()) {
                newAreaName.setInvalid(true);
                newAreaName.setErrorMessage(UI.getCurrent().getTranslation("event.missing_area_name"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && UI.getCurrent().getTranslation("select_existing_area").equals(areaChoice.getValue()) && areaSelect.getValue() == null) {
                areaSelect.setInvalid(true);
                areaSelect.setErrorMessage(UI.getCurrent().getTranslation("event.missing_area"));
                valid = false;
            } else if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && UI.getCurrent().getTranslation("create_new_area").equals(areaChoice.getValue()) && areaRepository
                    .findByWorld(campaign.getCampaignWorld())
                    .stream()
                    .anyMatch(area -> area.getAreaName().equalsIgnoreCase(newAreaName.getValue().trim()))) {
                newAreaName.setInvalid(true);
                newAreaName.setErrorMessage(UI.getCurrent().getTranslation("event.area_already_exists"));
                valid = false;
            } else {
                newAreaName.setInvalid(false);
                areaSelect.setInvalid(false);
            }

            // Validate Reoccurrence Type
            if (reoccurrenceTypeSelect.getValue() == null) {
                reoccurrenceTypeSelect.setInvalid(true);
                reoccurrenceTypeSelect.setErrorMessage(UI.getCurrent().getTranslation("event.missing_reoccurrence"));
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
            for (UIRegistryWithGrid.UIWithGrid uiWithGrid : UIRegistryWithGrid.getActiveUIs()) {
                if (uiWithGrid.getCampaignId().equals(campaign.getId())) {
                    uiWithGrid.getUi().access(() -> {
                        uiWithGrid.getEventGrid().setItems(eventRepository.findByCampaignId(campaign.getId()));
                    });
                }
            }

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
        if (UI.getCurrent().getTranslation("create_new_type").equals(eventTypeChoice.getValue())) {
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
        if (UI.getCurrent().getTranslation("create_new_place").equals(placeChoice.getValue()) && UI.getCurrent().getTranslation("create_new_area").equals(areaChoice.getValue())) {
            area = new Area();
            area.setAreaName(newAreaName.getValue().trim());
            area.setAreaDescription(newAreaDescription.getValue().trim());
            area.setAreaHistory(newAreaHistory.getValue().trim());
            area.setPrivateArea(newAreaPrivate.getValue());
            area.setWorld(campaign.getCampaignWorld());
            areaRepository.save(area);
        } else if (UI.getCurrent().getTranslation("create_new_type").equals(placeChoice.getValue()) && UI.getCurrent().getTranslation("select_existing_area").equals(areaChoice.getValue())) {
            area = areaSelect.getValue();
        }

        // Place
        Place place;
        if (UI.getCurrent().getTranslation("create_new_type").equals(placeChoice.getValue())) {
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
        EventDuration duration;
        if (event.getDuration() != null) {
            duration = event.getDuration();
        } else {
            duration = new EventDuration();
        }

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
        // System.out.println(event);
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

        H3 title = new H3(campaign.getCampaignName() + UI.getCurrent().getTranslation("players.title"));

        Grid<User> playerGrid = new Grid<>(User.class, false);
        playerGrid.setSizeFull();

        List<User> gms = userRepository.findByGmCampaigns(campaign);
        List<User> players = userRepository.findByPlayerCampaigns(campaign);

        Map<User, String> roleMap = new LinkedHashMap<>();
        gms.forEach(gm -> roleMap.put(gm, "GM"));
        players.forEach(player -> roleMap.put(player, UI.getCurrent().getTranslation("player")));

        playerGrid.addColumn(User::getUsername).setHeader(UI.getCurrent().getTranslation("username"));
        playerGrid.addColumn(User::getName).setHeader(UI.getCurrent().getTranslation("name"));
        playerGrid.addColumn(user -> roleMap.get(user)).setHeader(UI.getCurrent().getTranslation("role"));

        playerGrid.setItems(roleMap.keySet());

        layout.add(title, playerGrid);
        return layout;
    }

    private VerticalLayout createWorldPage(Campaign campaign) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        H3 title = new H3(campaign.getCampaignName() + UI.getCurrent().getTranslation("world.title"));
        Div placeholder = new Div(new Paragraph(UI.getCurrent().getTranslation("world.placeholder")));

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

