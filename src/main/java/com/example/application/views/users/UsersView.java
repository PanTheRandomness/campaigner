package com.example.application.views.users;

import com.example.application.data.User;
import com.example.application.data.Campaign;
import com.example.application.data.Role;
import com.example.application.data.repositories.UserRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.example.application.util.UIRegistryWithUserGrid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Users")
@Route("users")
@Menu(order = 5, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Composite<VerticalLayout> {

    private final UserRepository userRepository;

    public UsersView(UserRepository userRepository) {
        this.userRepository = userRepository;

        // TODO: add search
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Grid<User> userGrid = new Grid<>(User.class, false);
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
                .setHeader(getTranslation("username"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(User::getName)
                .setHeader(getTranslation("name"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(User::getEmail)
                .setHeader(getTranslation("email"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(user -> user.getGmCampaigns().stream()
                        .map(Campaign::getCampaignName)
                        .collect(Collectors.joining(", ")))
                .setHeader(getTranslation("gm_campaigns"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addColumn(user -> user.getPlayerCampaigns().stream()
                        .map(Campaign::getCampaignName)
                        .collect(Collectors.joining(", ")))
                .setHeader(getTranslation("player_campaigns"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);


        userGrid.addColumn(user -> user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", ")))
                .setHeader(getTranslation("roles"))
                .setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.START);

        userGrid.addComponentColumn(user -> {
            Button editButton = new Button(getTranslation("edit"), event -> openEditDialog(user));
            editButton.getStyle().set("margin-right", "8px");

            Button removeButton = new Button(getTranslation("remove"), event -> openRemoveDialog(user));
            removeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            return new Div(editButton, removeButton);
        }).setHeader(getTranslation("actions"));

        userGrid.addThemeVariants(
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_COMPACT
        );

        // Populate user grid
        userGrid.setItems(userRepository.findAll());

        UIRegistryWithUserGrid.register(UI.getCurrent(), userGrid);

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
        editDialog.setHeaderTitle(getTranslation("edituser"));
        editDialog.setWidth("400px");

        TextField usernameField = new TextField(getTranslation("username"));
        usernameField.setValue(user.getUsername() != null ? user.getUsername() : "");

        TextField nameField = new TextField(getTranslation("name"));
        nameField.setValue(user.getName() != null ? user.getName() : "");

        TextField emailField = new TextField(getTranslation("email"));
        emailField.setValue(user.getEmail() != null ? user.getEmail() : "");

        CheckboxGroup<Role> rolesGroup = new CheckboxGroup<>();
        rolesGroup.setLabel(getTranslation("roles"));
        rolesGroup.setItems(Role.values());
        rolesGroup.setValue(user.getRoles() != null ? user.getRoles() : Set.of());

        Image profileImage = new Image();
        profileImage.setWidth("150px");
        profileImage.setHeight("150px");

        if (user.getProfilePicture() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getProfilePicture());
            profileImage.setSrc("data:image/png;base64," + base64Image);
        }

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addSucceededListener(event -> {
            try (InputStream inputStream = buffer.getInputStream()) {
                byte[] imageBytes = inputStream.readAllBytes();
                user.setProfilePicture(imageBytes);

                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                profileImage.setSrc("data:" + event.getMIMEType() + ";base64," + base64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button saveButton = new Button(getTranslation("save"), event -> {
            user.setUsername(usernameField.getValue());
            user.setName(nameField.getValue());
            user.setEmail(emailField.getValue());
            user.setRoles(rolesGroup.getValue());

            userRepository.save(user);
            editDialog.close();
            pushUpdateToAllUsers();
        });

        Button cancelButton = new Button(getTranslation("cancel"), event -> editDialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout formLayout = new VerticalLayout(
                usernameField,
                nameField,
                emailField,
                rolesGroup,
                profileImage,
                upload,
                buttonLayout
        );

        editDialog.add(formLayout);
        editDialog.open();
    }

    private void openRemoveDialog(User user) {
        Dialog removeDialog = new Dialog();

        Text confirmationText = new Text(getTranslation("removeuser.confirmation"));

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button cancelButton = new Button(getTranslation("calcel"), event -> removeDialog.close());
        Button confirmButton = new Button(getTranslation("removeuser.yes"), event -> {
            userRepository.delete(user);
            removeDialog.close();
            pushUpdateToAllUsers();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttonLayout.add(confirmButton, cancelButton);
        buttonLayout.setSpacing(true);

        removeDialog.add(confirmationText, buttonLayout);
        removeDialog.open();
    }

    private void pushUpdateToAllUsers() {
        for (UIRegistryWithUserGrid.UIWithUserGrid uiWithUserGrid : UIRegistryWithUserGrid.getActiveUIs()) {
            uiWithUserGrid.getUi().access(() -> {
                uiWithUserGrid.getUserGrid().setItems(userRepository.findAll());
            });
        }
    }
}
