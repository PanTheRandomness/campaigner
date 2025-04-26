package com.example.application.views.campaigns;

import com.example.application.data.*;
import com.example.application.data.repositories.CalendarRepository;
import com.example.application.data.repositories.MoonRepository;
import com.example.application.data.repositories.UserRepository;
import com.example.application.data.repositories.WorldRepository;
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
import java.util.function.Consumer;

public class CampaignForm extends FormLayout {
    private final TextField nameField = new TextField(getTranslation("campaign_name"));
    private final TextArea descriptionField = new TextArea(getTranslation("campaign_description"));
    private final TextField gmField = new TextField(getTranslation("enter_gm"));
    private final VirtualList<User> currentGmsField = new VirtualList<>();
    private final TextField playerField = new TextField(getTranslation("enter_player"));
    private final VirtualList<User> currentPlayersField = new VirtualList<>();
    private final ComboBox<World> worldSelector = new ComboBox<>(getTranslation("select_world"));
    private final ComboBox<Calendar> calendarSelector = new ComboBox<>(getTranslation("select_calendar"));

    // New fields for creating a new world and calendar
    private final TextField newWorldField = new TextField(getTranslation("new_world_name"));
    private final TextField newCalendarField = new TextField(getTranslation("new_calendar_name"));
    private final TextField monthsInYearField = new TextField(getTranslation("months_in_year"));
    private final TextField daysInMonthField = new TextField(getTranslation("days_in_month"));
    private final TextField daysInWeekField = new TextField(getTranslation("days_in_week"));
    private final TextField moonCountField = new TextField(getTranslation("moon_count"));
    private final TextArea monthNamesField = new TextArea(getTranslation("month_names"));
    private final TextArea weekdayNamesField = new TextArea(getTranslation("weekday_names"));
    private final List<VerticalLayout> moonFields = new ArrayList<>();

    private final RadioButtonGroup<String> worldChoiceGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> calendarChoiceGroup = new RadioButtonGroup<>();

    private final WorldRepository worldRepository;
    private final CalendarRepository calendarRepository;
    private final MoonRepository moonRepository;

    private final Consumer<Campaign> onSave;

    private final List<User> selectedGms;
    private final List<User> selectedPlayers;

    private final Campaign existingCampaign;

    public CampaignForm(UserRepository userRepository, WorldRepository worldRepository,
                        CalendarRepository calendarRepository, MoonRepository moonRepository,
                        List<World> userWorlds, List<Calendar> userCalendars,  User loggedInUser,
                        Consumer<Campaign> onSave, Campaign existingCampaign) {
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

        // TODO: Add Close & Cancel-buttons

        this.selectedGms = new ArrayList<>();
        this.selectedPlayers = new ArrayList<>();
        selectedGms.add(loggedInUser);

        // Default visibility for world & calendar
        worldSelector.setVisible(false);
        calendarSelector.setVisible(false);
        newWorldField.setVisible(true);
        newCalendarField.setVisible(true);

        // Set up the RadioButtonGroups for selecting existing or creating new
        worldChoiceGroup.setLabel(getTranslation("select_world"));
        worldChoiceGroup.setItems(getTranslation("create_new"), getTranslation("select_existing"));
        worldChoiceGroup.setValue(getTranslation("create_new"));
        worldChoiceGroup.addValueChangeListener(event -> updateWorldFields(event.getValue()));

        calendarChoiceGroup.setLabel(getTranslation("select_calendar"));
        calendarChoiceGroup.setItems(getTranslation("create_new"), getTranslation("select_existing"));
        calendarChoiceGroup.addValueChangeListener(event -> updateCalendarFields(event.getValue()));

        worldSelector.setItems(userWorlds);
        worldSelector.setItemLabelGenerator(World::getWorldName);
        calendarSelector.setItems(userCalendars);
        calendarChoiceGroup.setValue(getTranslation("create_new"));
        calendarSelector.setItemLabelGenerator(Calendar::getCalendarName);
        // TODO: Add donjon calendar json-compatibility

        Button saveButton = new Button(getTranslation("save"));
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
                Button remove = new Button(getTranslation("remove_gm"), e -> {
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
            Button remove = new Button(getTranslation("remove_player"), e -> {
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

        Details gmDetails = new Details(getTranslation("current_gms"), gmListLayout);
        gmDetails.setOpened(true);

        Details playerDetails = new Details(getTranslation("current_players"), playerListLayout);
        playerDetails.setOpened(true);

        // Handle adding GMs
        Button addGmButton = new Button(getTranslation("add_gm"));
        addGmButton.addClickListener(e -> {
            User found = userRepository.findByUsername(gmField.getValue().trim()).orElse(null);
            if (found == null) {
                gmField.setInvalid(true);
                gmField.setErrorMessage(getTranslation("user.not_found"));
            } else if (selectedGms.contains(found)) {
                gmField.setInvalid(true);
                gmField.setErrorMessage(getTranslation("user.already_gm"));
            } else {
                selectedGms.add(found);
                currentGmsField.setItems(selectedGms);
                gmField.clear();
                gmField.setInvalid(false);
            }
        });

        // TODO: Fix bug previous players disappear from player list after adding a player in edit mode
        // Handle adding Players
        Button addPlayerButton = new Button(getTranslation("add_player"));
        addPlayerButton.addClickListener(e -> {
            User found = userRepository.findByUsername(playerField.getValue().trim()).orElse(null);
            if (found == null) {
                playerField.setInvalid(true);
                playerField.setErrorMessage(getTranslation("user.not_found"));
            } else if (selectedGms.contains(found)) {
                playerField.setInvalid(true);
                playerField.setErrorMessage(getTranslation("user.already_gm"));
            } else if (selectedPlayers.contains(found)) {
                playerField.setInvalid(true);
                playerField.setErrorMessage(getTranslation("user.already_player"));
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

            // TODO: Fix moon fields going below save button
            // TODO: Fix field amount if moon count lessens
            else if (newMoonCount < moonFields.size()) {
                moonFields.subList(newMoonCount, moonFields.size()).clear();
            }
        });

        // Add components to the form
        H2 title = new H2(getTranslation("campaign_info"));
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

        // TODO: Validate with binders?
        // Validate campaign name
        if (nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            nameField.setErrorMessage(getTranslation("campaign.missing_name"));
            isValid = false;
        } else {
            nameField.setInvalid(false);
        }

        // Validate world selection
        if (getTranslation("create_new").equals(worldChoiceGroup.getValue()) && newWorldField.getValue().trim().isEmpty()) {
            newWorldField.setInvalid(true);
            newWorldField.setErrorMessage(getTranslation("campaign.missing_world_name"));
            isValid = false;
        } else if (getTranslation("select_existing").equals(worldChoiceGroup.getValue()) && worldSelector.getValue() == null) {
            worldSelector.setInvalid(true);
            worldSelector.setErrorMessage(getTranslation("campaign.missing_world"));
            isValid = false;
        } else {
            newWorldField.setInvalid(false);
            worldSelector.setInvalid(false);
        }

        // Validate calendar selection
        if (getTranslation("create_new").equals(calendarChoiceGroup.getValue()) && newCalendarField.getValue().trim().isEmpty()) {
            newCalendarField.setInvalid(true);
            newCalendarField.setErrorMessage(getTranslation("campaign.missing_calendar_name"));
            isValid = false;
        } else if (getTranslation("select_existing").equals(calendarChoiceGroup.getValue()) && calendarSelector.getValue() == null) {
            calendarSelector.setInvalid(true);
            calendarSelector.setErrorMessage(getTranslation("campaign.missing_calendar"));
            isValid = false;
        } else {
            newCalendarField.setInvalid(false);
            calendarSelector.setInvalid(false);
        }

        // Validate months, days and names if a new calendar is being created
        if (getTranslation("create_new").equals(calendarChoiceGroup.getValue())) {
            int monthsInYear = Integer.parseInt(monthsInYearField.getValue());
            int daysInWeek = Integer.parseInt(daysInWeekField.getValue());
            List<String> monthNames = List.of(monthNamesField.getValue().split(","));
            List<String> weekdayNames = List.of(weekdayNamesField.getValue().split(","));

            // Validate monthsInYear vs monthNames
            if (monthsInYear != monthNames.size()) {
                monthsInYearField.setInvalid(true);
                monthsInYearField.setErrorMessage(getTranslation("campaign.month_count_names_nomatch"));
                isValid = false;
            } else {
                monthsInYearField.setInvalid(false);
            }

            // Validate daysInWeek vs weekdayNames
            if (daysInWeek != weekdayNames.size()) {
                daysInWeekField.setInvalid(true);
                daysInWeekField.setErrorMessage(getTranslation("campaign.weekday_count_names_nomatch"));
                isValid = false;
            } else {
                daysInWeekField.setInvalid(false);
            }
        }

        // Validate moons if a new calendar is being created
        if (getTranslation("create_new").equals(calendarChoiceGroup.getValue())) {
            for (VerticalLayout moonLayout : moonFields) {
                TextField moonNameField = (TextField) moonLayout.getChildren().findFirst().orElse(null);
                if (moonNameField != null && moonNameField.getValue().trim().isEmpty()) {
                    moonNameField.setInvalid(true);
                    moonNameField.setErrorMessage(getTranslation("campaign.missing_moon_name"));
                    isValid = false;
                } else {
                    assert moonNameField != null;
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
        World selectedWorld;
        if (getTranslation("create_new").equals(worldChoiceGroup.getValue())) {
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
        Calendar selectedCalendar;
        if (getTranslation("create_new").equals(calendarChoiceGroup.getValue())) {
            selectedCalendar = new Calendar();
            selectedCalendar.setCalendarName(newCalendarField.getValue().trim());
            selectedCalendar.setMonthsInYear(Integer.parseInt(monthsInYearField.getValue()));
            selectedCalendar.setDaysInMonth(Integer.parseInt(daysInMonthField.getValue()));
            selectedCalendar.setDaysInWeek(Integer.parseInt(daysInWeekField.getValue()));
            selectedCalendar.setMonthNames(List.of(monthNamesField.getValue().split(",")));
            selectedCalendar.setWeekdayNames(List.of(weekdayNamesField.getValue().split(",")));
            calendarRepository.save(selectedCalendar);
            // Create moons
            List<Moon> moons = new ArrayList<>();
            int moonCount = Integer.parseInt(moonCountField.getValue());
            for (int i = 0; i < moonCount; i++) {
                VerticalLayout moonLayout = moonFields.get(i);
                TextField moonNameField = (TextField) moonLayout.getChildren().findFirst().orElse(null);
                TextField cycleField = (TextField) moonLayout.getChildren().skip(1).findFirst().orElse(null);
                TextField shiftField = (TextField) moonLayout.getChildren().skip(2).findFirst().orElse(null);

                Moon moon = new Moon();
                assert moonNameField != null;
                moon.setMoonName(moonNameField.getValue());
                assert cycleField != null;
                moon.setCycle(Double.parseDouble(cycleField.getValue()));
                assert shiftField != null;
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
        boolean isCreateNew = getTranslation("create_new").equals(choice);
        worldSelector.setVisible(!isCreateNew);
        newWorldField.setVisible(isCreateNew);
    }

    private void updateCalendarFields(String choice) {
        boolean isCreateNew = getTranslation("create_new").equals(choice);
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
        moonLayout.add(new TextField(getTranslation("moon_name")), new TextField(getTranslation("cycle")), new TextField(getTranslation("shift")));
        moonFields.add(moonLayout);
        add(moonLayout);
    }

    public void fillFormWithCampaignData(Campaign campaign) {
        nameField.setValue(campaign.getCampaignName());
        descriptionField.setValue(campaign.getCampaignDescription());

        // Users
        List<User> gms = campaign.getGms();
        List<User> players = campaign.getPlayers();
        currentGmsField.setItems(gms);
        currentPlayersField.setItems(players);

        // World
        World world = campaign.getCampaignWorld();
        if (worldRepository.findById(world.getId()).isPresent()) {
            worldChoiceGroup.setValue(getTranslation("select_existing"));
            worldSelector.setValue(world);
        } else {
            worldChoiceGroup.setValue(getTranslation("create_new"));
            newWorldField.setValue(world.getWorldName());
        }

        // Calendar
        Calendar calendar = campaign.getCalendar();
        if (calendarRepository.findById(calendar.getId()).isPresent()) {
            calendarChoiceGroup.setValue(getTranslation("select_existing"));
            calendarSelector.setValue(calendar);
        } else {
            calendarChoiceGroup.setValue(getTranslation("create_new"));
            newCalendarField.setValue(calendar.getCalendarName());

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
                TextField moonName = new TextField(getTranslation("moon_name"));
                moonName.setValue(moon.getMoonName());

                TextField cycle = new TextField(getTranslation("cycle"));
                cycle.setValue(String.valueOf(moon.getCycle()));

                TextField shift = new TextField(getTranslation("shift"));
                shift.setValue(String.valueOf(moon.getShift()));

                VerticalLayout moonLayout = new VerticalLayout(moonName, cycle, shift);
                moonFields.add(moonLayout);
            }
        }
    }
}
