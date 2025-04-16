package com.example.application.views.users;

import com.example.application.data.User;
import com.example.application.data.Campaign;
import com.example.application.data.repositories.UserRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Users")
@Route("users")
@Menu(order = 6, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Composite<VerticalLayout> {

    private final UserRepository userRepository;
    private Grid<User> userGrid;

    public UsersView(UserRepository userRepository) {
        this.userRepository = userRepository;

        VerticalLayout layoutColumn2 = new VerticalLayout();
        userGrid = new Grid<>(User.class, false);
        HorizontalLayout layoutRow = new HorizontalLayout();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");

        // Configure user grid
        userGrid.setWidth("100%");
        userGrid.setHeight("100%");
        userGrid.getStyle().set("font-size", "var(--lumo-font-size-l)");

        userGrid.addColumn(User::getUsername)
                .setHeader("Username")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(User::getName)
                .setHeader("Name")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(User::getEmail)
                .setHeader("Email")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(user -> user.getGmCampaigns().stream()
                        .map(Campaign::getCampaignName)
                        .collect(Collectors.joining(", ")))
                .setHeader("GM Campaigns")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(user -> user.getPlayerCampaigns().stream()
                        .map(Campaign::getCampaignName)
                        .collect(Collectors.joining(", ")))
                .setHeader("Player Campaigns")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);


        userGrid.addColumn(user -> user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", ")))
                .setHeader("Roles")
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addComponentColumn(user -> {
            Button editButton = new Button("Edit", event -> openEditDialog(user));
            editButton.getStyle().set("margin-right", "8px");

            Button removeButton = new Button("Remove", event -> openRemoveDialog(user));
            removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            return new Div(editButton, removeButton);
        }).setHeader("Actions");

        userGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COMPACT
        );

        // Populate user grid
        userGrid.setItems(userRepository.findAll());

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");

        // Add components to layout
        getContent().add(layoutColumn2);
        layoutColumn2.add(userGrid);
        getContent().add(layoutRow);
    }

    private void openEditDialog(User user) {
        Dialog editDialog = new Dialog();
        TextField nameField = new TextField("Name", user.getName());
        TextField emailField = new TextField("Email", user.getEmail());

        Button saveButton = new Button("Save", event -> {
            user.setName(nameField.getValue());
            user.setEmail(emailField.getValue());
            userRepository.save(user);
            editDialog.close();
        });

        Button cancelButton = new Button("Cancel", event -> editDialog.close());

        editDialog.add(nameField, emailField, saveButton, cancelButton);
        editDialog.open();
    }

    private void openRemoveDialog(User user) {
        Dialog removeDialog = new Dialog();

        Text confirmationText = new Text("Are you sure you want to remove this user?");

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button cancelButton = new Button("Cancel", event -> removeDialog.close());
        Button confirmButton = new Button("Yes, remove", event -> {
            userRepository.delete(user);
            removeDialog.close();
            updateUserGrid();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttonLayout.add(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);

        removeDialog.add(confirmationText, buttonLayout);
        removeDialog.open();
    }

    private void updateUserGrid() {
        List<User> users = userRepository.findAll();
        userGrid.setItems(users);
    }
}
