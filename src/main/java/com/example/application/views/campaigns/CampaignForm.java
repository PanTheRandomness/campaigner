package com.example.application.views.campaigns;

import com.example.application.data.*;
import com.example.application.data.repositories.CalendarRepository;
import com.example.application.data.repositories.MoonRepository;
import com.example.application.data.repositories.UserRepository;
import com.example.application.data.repositories.WorldRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CampaignForm extends FormLayout {
    private H2 title = new H2("Campaign Info");
    private TextField nameField = new TextField("Campaign Name");
    private final TextArea descriptionField = new TextArea("Campaign Description");
    private final TextField gmField = new TextField("Enter GM Username");
    private final VirtualList<User> currentGmsField = new VirtualList<>();
    private final Button addGmButton = new Button("Add GM");
    private final TextField playerField = new TextField("Enter Player Username");
    private final VirtualList<User> currentPlayersField = new VirtualList<>();
    private final Button addPlayerButton = new Button("Add Player");
    private final ComboBox<World> worldSelector = new ComboBox<>("Select World");
    private final ComboBox<Calendar> calendarSelector = new ComboBox<>("Select Calendar");
    private final Button saveButton = new Button("Save");

    // New fields for creating a new world and calendar
    private final TextField newWorldField = new TextField("New World Name");
    private final TextField newCalendarField = new TextField("New Calendar Name");
    private final TextField monthsInYearField = new TextField("Months in Year");
    private final TextField daysInMonthField = new TextField("Days in Month");
    private final TextField daysInWeekField = new TextField("Days in Week");
    private final TextField moonCountField = new TextField("Number of Moons");
    private final TextArea monthNamesField = new TextArea("Month Names (comma-separated)");
    private final TextArea weekdayNamesField = new TextArea("Weekday Names (comma-separated)");
    private final List<VerticalLayout> moonFields = new ArrayList<>();

    private final RadioButtonGroup<String> worldChoiceGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> calendarChoiceGroup = new RadioButtonGroup<>();

    private final UserRepository userRepository;
    private final WorldRepository worldRepository;
    private final CalendarRepository calendarRepository;
    private final MoonRepository moonRepository;

    private Consumer<Campaign> onSave;

    private List<User> selectedGms;
    private List<User> selectedPlayers;

    private Campaign existingCampaign;

    public CampaignForm(UserRepository userRepository, WorldRepository worldRepository,
                        CalendarRepository calendarRepository, MoonRepository moonRepository,
                        List<World> userWorlds, List<Calendar> userCalendars,  User loggedInUser,
                        Consumer<Campaign> onSave, Campaign existingCampaign) {
        this.userRepository = userRepository;
        this.worldRepository = worldRepository;
        this.calendarRepository = calendarRepository;
        this.moonRepository = moonRepository;
        this.onSave = onSave;
        this.existingCampaign = existingCampaign;

        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("gap", "1rem");
        getStyle().set("padding", "1rem");
        setWidth("400px");

        this.selectedGms = new ArrayList<>();
        this.selectedPlayers = new ArrayList<>();
        selectedGms.add(loggedInUser);

        // Default visibility for world & calendar
        worldSelector.setVisible(false);
        calendarSelector.setVisible(false);
        newWorldField.setVisible(true);
        newCalendarField.setVisible(true);

        // Set up the RadioButtonGroups for selecting existing or creating new
        worldChoiceGroup.setLabel("Choose World");
        worldChoiceGroup.setItems("Create New", "Select Existing");
        worldChoiceGroup.setValue("Create New");
        worldChoiceGroup.addValueChangeListener(event -> updateWorldFields(event.getValue()));

        calendarChoiceGroup.setLabel("Choose Calendar");
        calendarChoiceGroup.setItems("Create New", "Select Existing");
        calendarChoiceGroup.addValueChangeListener(event -> updateCalendarFields(event.getValue()));

        worldSelector.setItems(userWorlds);
        worldSelector.setItemLabelGenerator(World::getWorldName);
        calendarSelector.setItems(userCalendars);
        calendarChoiceGroup.setValue("Create New");
        calendarSelector.setItemLabelGenerator(Calendar::getCalendarName);
        // TODO: Add donjon calendar json-compatibility

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveCampaign());

        // Setup VirtualLists
        currentGmsField.setItems(selectedGms);
        currentPlayersField.setItems(selectedPlayers);

        currentGmsField.setRenderer(new ComponentRenderer<>(user -> {
            HorizontalLayout layout = new HorizontalLayout();
            Span name = new Span(user.getUsername());

            layout.add(name);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            // Cannot delete creating user from GM list
            if (!user.getId().equals(loggedInUser.getId())) {
                Button remove = new Button("Remove GM", e -> {
                    selectedGms.remove(user);
                    currentGmsField.setItems(selectedGms);
                });
                remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
                layout.add(remove);
            }

            return layout;
        }));

        currentPlayersField.setRenderer(new ComponentRenderer<>(user -> {
            HorizontalLayout layout = new HorizontalLayout();
            Span name = new Span(user.getUsername());
            Button remove = new Button("Remove Player", e -> {
                selectedPlayers.remove(user);
                currentPlayersField.setItems(selectedPlayers);
            });
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
            layout.add(name, remove);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            return layout;
        }));

        currentGmsField.setHeight("150px");
        currentPlayersField.setHeight("150px");

        VerticalLayout gmListLayout = new VerticalLayout(currentGmsField);
        gmListLayout.setPadding(false);
        gmListLayout.setSpacing(false);

        VerticalLayout playerListLayout = new VerticalLayout(currentPlayersField);
        playerListLayout.setPadding(false);
        playerListLayout.setSpacing(false);

        Details gmDetails = new Details("Current GMs", gmListLayout);
        gmDetails.setOpened(true);

        Details playerDetails = new Details("Current Players", playerListLayout);
        playerDetails.setOpened(true);

        // Handle adding GMs
        addGmButton.addClickListener(e -> {
            User found = userRepository.findByUsername(gmField.getValue().trim()).orElse(null);
            if (found == null) {
                gmField.setInvalid(true);
                gmField.setErrorMessage("User not found.");
            } else if (selectedGms.contains(found)) {
                gmField.setInvalid(true);
                gmField.setErrorMessage("User is already a GM.");
            } else {
                selectedGms.add(found);
                currentGmsField.setItems(selectedGms);
                gmField.clear();
                gmField.setInvalid(false);
            }
        });

        // Handle adding Players
        addPlayerButton.addClickListener(e -> {
            User found = userRepository.findByUsername(playerField.getValue().trim()).orElse(null);
            if (found == null) {
                playerField.setInvalid(true);
                playerField.setErrorMessage("User not found.");
            } else if (selectedGms.contains(found)) {
                playerField.setInvalid(true);
                playerField.setErrorMessage("User is already a GM.");
            } else if (selectedPlayers.contains(found)) {
                playerField.setInvalid(true);
                playerField.setErrorMessage("User is already a Player.");
            } else {
                selectedPlayers.add(found);
                currentPlayersField.setItems(selectedPlayers);
                playerField.clear();
                playerField.setInvalid(false);
            }
        });

        // Handle adding moon fields
        moonCountField.addValueChangeListener(event -> {
            String moonCountValue = moonCountField.getValue();
            int newMoonCount = Integer.parseInt(moonCountValue);

            // Moon count grows
            if (newMoonCount > moonFields.size()) {
                for (int i = moonFields.size(); i < newMoonCount; i++) {
                    addMoonFields();
                }
            }

            // TODO: Fix field amount if moon count lessens
            else if (newMoonCount < moonFields.size()) {
                for (int i = moonFields.size() - 1; i >= newMoonCount; i--) {
                    moonFields.remove(i);
                }
            }
        });

        // Add components to the form
        add(
                title,
                nameField, descriptionField,
                worldChoiceGroup, worldSelector, newWorldField,
                calendarChoiceGroup, calendarSelector, newCalendarField,
                monthsInYearField, daysInMonthField, monthNamesField, daysInWeekField, weekdayNamesField, moonCountField,
                currentGmsField, gmField, addGmButton,
                currentPlayersField, playerField, addPlayerButton,
                saveButton
        );
    }

    private void saveCampaign() {
        boolean isValid = true;

        // Validate campaign name
        if (nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage("Campaign name cannot be empty.");
            isValid = false;
        } else {
            nameField.setInvalid(false);
        }

        // Validate world selection
        if ("Create New".equals(worldChoiceGroup.getValue()) && newWorldField.getValue().trim().isEmpty()) {
            newWorldField.setInvalid(true);
            newWorldField.setErrorMessage("Please enter a new world name.");
            isValid = false;
        } else if ("Select Existing".equals(worldChoiceGroup.getValue()) && worldSelector.getValue() == null) {
            worldSelector.setInvalid(true);
            worldSelector.setErrorMessage("Please select a world.");
            isValid = false;
        } else {
            newWorldField.setInvalid(false);
            worldSelector.setInvalid(false);
        }

        // Validate calendar selection
        if ("Create New".equals(calendarChoiceGroup.getValue()) && newCalendarField.getValue().trim().isEmpty()) {
            newCalendarField.setInvalid(true);
            newCalendarField.setErrorMessage("Please enter a new calendar name.");
            isValid = false;
        } else if ("Select Existing".equals(calendarChoiceGroup.getValue()) && calendarSelector.getValue() == null) {
            calendarSelector.setInvalid(true);
            calendarSelector.setErrorMessage("Please select a calendar.");
            isValid = false;
        } else {
            newCalendarField.setInvalid(false);
            calendarSelector.setInvalid(false);
        }

        // Validate months, days and names if a new calendar is being created
        if ("Create New".equals(calendarChoiceGroup.getValue())) {
            int monthsInYear = Integer.parseInt(monthsInYearField.getValue());
            int daysInWeek = Integer.parseInt(daysInWeekField.getValue());
            List<String> monthNames = List.of(monthNamesField.getValue().split(","));
            List<String> weekdayNames = List.of(weekdayNamesField.getValue().split(","));

            // Validate monthsInYear vs monthNames
            if (monthsInYear != monthNames.size()) {
                monthsInYearField.setInvalid(true);
                monthsInYearField.setErrorMessage("Months in year must match the number of month names.");
                isValid = false;
            } else {
                monthsInYearField.setInvalid(false);
            }

            // Validate daysInWeek vs weekdayNames
            if (daysInWeek != weekdayNames.size()) {
                daysInWeekField.setInvalid(true);
                daysInWeekField.setErrorMessage("Days in week must match the number of weekday names.");
                isValid = false;
            } else {
                daysInWeekField.setInvalid(false);
            }
        }

        // Validate moons if a new calendar is being created
        if ("Create New".equals(calendarChoiceGroup.getValue())) {
            List<Moon> moons = new ArrayList<>();
            for (VerticalLayout moonLayout : moonFields) {
                TextField moonNameField = (TextField) moonLayout.getChildren().findFirst().orElse(null);
                if (moonNameField != null && moonNameField.getValue().trim().isEmpty()) {
                    moonNameField.setInvalid(true);
                    moonNameField.setErrorMessage("Moon name cannot be empty.");
                    isValid = false;
                } else {
                    moonNameField.setInvalid(false);
                }
            }
        }

        if (isValid) {
            // Campaign
            Campaign campaign = (existingCampaign != null) ? existingCampaign : new Campaign();
            campaign.setCampaignName(nameField.getValue());
            campaign.setCampaignDescription(descriptionField.getValue());

            // Handle World
            World selectedWorld = handleWorldSelection();
            campaign.setCampaignWorld(selectedWorld);

            // Handle Calendar
            Calendar selectedCalendar = handleCalendarSelection();
            campaign.setCalendar(selectedCalendar);

            // Add GM(s) & Players
            campaign.setGms(selectedGms);
            campaign.setPlayers(selectedPlayers);

            // Call onSave action passed in the constructor
            onSave.accept(campaign);
        }
    }

    // Handle World creation or selection
    private World handleWorldSelection() {
        World selectedWorld = null;
        if ("Create New".equals(worldChoiceGroup.getValue())) {
            String newWorldName = newWorldField.getValue().trim();
            selectedWorld = new World();
            selectedWorld.setWorldName(newWorldName);
            selectedWorld = worldRepository.save(selectedWorld);
        } else {
            selectedWorld = worldSelector.getValue();
        }
        return selectedWorld;
    }

    // Handle Calendar creation or selection
    private Calendar handleCalendarSelection() {
        Calendar selectedCalendar = null;
        if ("Create New".equals(calendarChoiceGroup.getValue())) {
            selectedCalendar = new Calendar();
            selectedCalendar.setCalendarName(newCalendarField.getValue().trim());
            selectedCalendar.setMonthsInYear(Integer.parseInt(monthsInYearField.getValue()));
            selectedCalendar.setDaysInMonth(Integer.parseInt(daysInMonthField.getValue()));
            selectedCalendar.setDaysInWeek(Integer.parseInt(daysInWeekField.getValue()));
            selectedCalendar.setMonthNames(List.of(monthNamesField.getValue().split(",")));
            selectedCalendar.setWeekdayNames(List.of(weekdayNamesField.getValue().split(",")));

            // Create moons
            List<Moon> moons = new ArrayList<>();
            int moonCount = Integer.parseInt(moonCountField.getValue());
            for (int i = 0; i < moonCount; i++) {
                VerticalLayout moonLayout = moonFields.get(i);
                TextField moonNameField = (TextField) moonLayout.getChildren().findFirst().orElse(null);
                TextField cycleField = (TextField) moonLayout.getChildren().skip(1).findFirst().orElse(null);
                TextField shiftField = (TextField) moonLayout.getChildren().skip(2).findFirst().orElse(null);

                Moon moon = new Moon();
                moon.setMoonName(moonNameField.getValue());
                moon.setCycle(Double.parseDouble(cycleField.getValue()));
                moon.setShift(Integer.parseInt(shiftField.getValue()));
                moon.setCalendar(selectedCalendar);
                moons.add(moon);
                moonRepository.save(moon);
            }

            selectedCalendar.setMoons(moons);
            selectedCalendar = calendarRepository.save(selectedCalendar);
        } else {
            selectedCalendar = calendarSelector.getValue();
        }
        return selectedCalendar;
    }


    private void updateWorldFields(String choice) {
        boolean isCreateNew = "Create New".equals(choice);
        worldSelector.setVisible(!isCreateNew);
        newWorldField.setVisible(isCreateNew);
    }

    private void updateCalendarFields(String choice) {
        boolean isCreateNew = "Create New".equals(choice);
        calendarSelector.setVisible(!isCreateNew);
        newCalendarField.setVisible(isCreateNew);
        calendarSelector.setVisible(!isCreateNew);
        monthsInYearField.setVisible(isCreateNew);
        daysInMonthField.setVisible(isCreateNew);
        daysInWeekField.setVisible(isCreateNew);
        moonCountField.setVisible(isCreateNew);
        monthNamesField.setVisible(isCreateNew);
        weekdayNamesField.setVisible(isCreateNew);
    }

    private void addMoonFields() {
        VerticalLayout moonLayout = new VerticalLayout();
        moonLayout.add(new TextField("Moon Name"), new TextField("Cycle"), new TextField("Shift"));
        moonFields.add(moonLayout);
        add(moonLayout);
    }

    public void fillFormWithCampaignData(Campaign campaign) {
        nameField.setValue(campaign.getCampaignName());
        descriptionField.setValue(campaign.getCampaignDescription());

        // Users
        List<User> gms = campaign.getGms() != null ? campaign.getGms() : new ArrayList<>();
        List<User> players = campaign.getPlayers() != null ? campaign.getPlayers() : new ArrayList<>();
        currentGmsField.setItems(gms);
        currentPlayersField.setItems(players);

        // World
        World world = campaign.getCampaignWorld();
        if (world != null) {
            if (worldRepository.findById(world.getId()).isPresent()) {
                worldChoiceGroup.setValue("Select Existing");
                worldSelector.setValue(world);
            } else {
                worldChoiceGroup.setValue("Create New");
                newWorldField.setValue(world.getWorldName() != null ? world.getWorldName() : "");
            }
        }

        // Calendar
        Calendar calendar = campaign.getCalendar();
        if (calendar != null) {
            if (calendarRepository.findById(calendar.getId()).isPresent()) {
                calendarChoiceGroup.setValue("Select Existing");
                calendarSelector.setValue(calendar);
            } else {
                calendarChoiceGroup.setValue("Create New");
                newCalendarField.setValue(calendar.getCalendarName() != null ? calendar.getCalendarName() : "");

                monthsInYearField.setValue(String.valueOf(calendar.getMonthsInYear()));
                daysInMonthField.setValue(String.valueOf(calendar.getDaysInMonth()));
                daysInWeekField.setValue(String.valueOf(calendar.getDaysInWeek()));
                monthNamesField.setValue(String.join(",", calendar.getMonthNames()));
                weekdayNamesField.setValue(String.join(",", calendar.getWeekdayNames()));

                // Moons
                List<Moon> moons = calendar.getMoons();
                moonCountField.setValue(String.valueOf(moons.size()));
                moonFields.clear();

                for (Moon moon : moons) {
                    TextField moonName = new TextField("Moon Name");
                    moonName.setValue(moon.getMoonName());

                    TextField cycle = new TextField("Cycle");
                    cycle.setValue(String.valueOf(moon.getCycle()));

                    TextField shift = new TextField("Shift");
                    shift.setValue(String.valueOf(moon.getShift()));

                    VerticalLayout moonLayout = new VerticalLayout(moonName, cycle, shift);
                    moonFields.add(moonLayout);
                }
            }
        }
    }
}
