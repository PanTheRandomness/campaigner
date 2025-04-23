package com.example.application.views.campaigns;

import com.example.application.data.Calendar;
import com.example.application.data.Campaign;
import com.example.application.data.User;
import com.example.application.data.World;
import com.example.application.data.repositories.UserRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
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
    private final RadioButtonGroup<String> worldChoiceGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> calendarChoiceGroup = new RadioButtonGroup<>();

    private final UserRepository userRepository;

    public CampaignForm(UserRepository userRepository, List<World> userWorlds, List<Calendar> userCalendars,  User loggedInUser, Consumer<Campaign> onSave) {
        this.userRepository = userRepository;

        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("gap", "1rem");
        getStyle().set("padding", "1rem");
        setWidth("400px");

        List<User> selectedGms = new ArrayList<>();
        List<User> selectedPlayers = new ArrayList<>();
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
        // TODO: Add Moons to Calendar creation
        // TODO: Add donjon calendar json-compatibility

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
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
            // TODO: Create new world to db
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
            // TODO: Create new calendar to db
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

            if (isValid) {
                Campaign campaign = new Campaign();
                campaign.setCampaignName(nameField.getValue());
                campaign.setCampaignDescription(descriptionField.getValue());

                // Add GM(s) & Players
                campaign.setGms(selectedGms);
                campaign.setPlayers(selectedPlayers);

                onSave.accept(campaign);
            }
        });

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

        // Add components to the form
        add(
                nameField, descriptionField,
                worldChoiceGroup, worldSelector, newWorldField,
                calendarChoiceGroup, calendarSelector, newCalendarField,
                currentGmsField, gmField, addGmButton,
                currentPlayersField, playerField, addPlayerButton,
                saveButton
        );
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
    }
}
