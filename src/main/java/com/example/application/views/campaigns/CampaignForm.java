package com.example.application.views.campaigns;

import com.example.application.data.Calendar;
import com.example.application.data.Campaign;
import com.example.application.data.User;
import com.example.application.data.World;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CampaignForm extends FormLayout {
    private TextField nameField = new TextField("Campaign Name");
    private final TextArea descriptionField = new TextArea("Campaign Description");
    private final TextField gmField = new TextField("Enter GM Username or Email");
    private final TextField playerField = new TextField("Enter Player Username or Email");
    private final ComboBox<World> worldSelector = new ComboBox<>("Select World");
    private final ComboBox<Calendar> calendarSelector = new ComboBox<>("Select Calendar");
    private final Button saveButton = new Button("Save");

    // New fields for creating a new world and calendar
    private final TextField newWorldField = new TextField("New World Name");
    private final TextField newCalendarField = new TextField("New Calendar Name");
    private final RadioButtonGroup<String> worldChoiceGroup = new RadioButtonGroup<>();
    private final RadioButtonGroup<String> calendarChoiceGroup = new RadioButtonGroup<>();

    // TODO: Add rest of the new world/Calendar fields
    // TODO: Add donjon calendar json import
    // TODO: Add required-validation to fields

    public CampaignForm(List<User> allUsers, List<World> userWorlds, List<Calendar> userCalendars,  User loggedInUser, Consumer<Campaign> onSave) {
        getStyle().set("display", "flex");
        getStyle().set("flexDirection", "column");
        getStyle().set("gap", "1rem");
        getStyle().set("padding", "1rem");
        setWidth("400px");

        // Set up the RadioButtonGroups for selecting existing or creating new
        worldChoiceGroup.setLabel("Choose World");
        worldChoiceGroup.setItems("Select Existing", "Create New");
        worldChoiceGroup.addValueChangeListener(event -> updateWorldFields(event.getValue()));

        calendarChoiceGroup.setLabel("Choose Calendar");
        calendarChoiceGroup.setItems("Select Existing", "Create New");
        calendarChoiceGroup.addValueChangeListener(event -> updateCalendarFields(event.getValue()));

        worldSelector.setItems(userWorlds);
        calendarSelector.setItems(userCalendars);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> {
            Campaign campaign = new Campaign();
            campaign.setCampaignName(nameField.getValue());
            campaign.setCampaignDescription(descriptionField.getValue());

            // Add GM(s) & Players
            List<User> selectedGms = new ArrayList<>();
            List<User> selectedPlayers = new ArrayList<>();
            String gmInput = gmField.getValue();
            String playerInput = playerField.getValue();

            for (User user : allUsers) {
                if (user.getUsername().equalsIgnoreCase(gmInput) || user.getEmail().equalsIgnoreCase(gmInput)) {
                    selectedGms.add(user);
                }
                if (user.getUsername().equalsIgnoreCase(playerInput) || user.getEmail().equalsIgnoreCase(playerInput)) {
                    selectedPlayers.add(user);
                }
            }

            campaign.setGms(selectedGms);
            campaign.setPlayers(selectedPlayers);

            onSave.accept(campaign);
        });

        add(nameField, descriptionField, gmField, playerField, worldChoiceGroup, worldSelector, newWorldField, calendarChoiceGroup, calendarSelector, newCalendarField, saveButton);
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
