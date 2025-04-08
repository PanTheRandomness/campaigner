package com.example.application.views.register;

import com.example.application.data.Role;
import com.example.application.data.User;
import com.example.application.services.UserService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Set;

@PageTitle("Register")
@Route("register")
@AnonymousAllowed
public class RegisterView extends Composite<VerticalLayout> {

    public RegisterView(UserService userService, PasswordEncoder passwordEncoder) {

        VerticalLayout layoutColumn2 = new VerticalLayout();
        H2 h2 = new H2();
        TextField usernameField = new TextField("Username");
        TextField nameField = new TextField("Name");
        EmailField emailField = new EmailField("Email");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField passwordField2 = new PasswordField("Password again");

        HorizontalLayout layoutRow = new HorizontalLayout();
        Button buttonSecondary = new Button();
        Button buttonPrimary = new Button();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Paragraph textSmall = new Paragraph();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutColumn2.addClassName(Gap.SMALL);
        layoutColumn2.addClassName(Padding.SMALL);
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        layoutColumn2.setJustifyContentMode(JustifyContentMode.CENTER);
        layoutColumn2.setAlignItems(Alignment.CENTER);

        h2.setText("Register");
        layoutColumn2.setAlignSelf(FlexComponent.Alignment.CENTER, h2);
        h2.setWidth("max-content");

        usernameField.setLabel("Username");
        usernameField.setWidth("min-content");

        nameField.setLabel("Name");
        nameField.setWidth("min-content");

        emailField.setLabel("Email");
        emailField.setWidth("min-content");

        passwordField.setLabel("Password");
        passwordField.setWidth("min-content");

        passwordField2.setLabel("Password again");
        passwordField2.setWidth("min-content");

        layoutRow.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutRow.setAlignItems(Alignment.START);
        layoutRow.setJustifyContentMode(JustifyContentMode.CENTER);

        buttonSecondary.setText("Cancel");
        buttonSecondary.setWidth("min-content");
        buttonPrimary.setText("Register");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        textSmall.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");

        getContent().add(layoutColumn2);
        layoutColumn2.add(h2);
        layoutColumn2.add(usernameField);
        layoutColumn2.add(nameField);
        layoutColumn2.add(emailField);
        layoutColumn2.add(passwordField);
        layoutColumn2.add(passwordField2);
        layoutColumn2.add(layoutRow);
        layoutRow.add(buttonSecondary);
        layoutRow.add(buttonPrimary);
        getContent().add(layoutRow2);
        layoutRow2.add(textSmall);

        buttonPrimary.addClickListener(e -> {
            String username = usernameField.getValue().trim();
            String name = nameField.getValue().trim();
            String email = emailField.getValue().trim();
            String password = passwordField.getValue();
            String confirmPassword = passwordField2.getValue();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                textSmall.setText("Please fill all required fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                textSmall.setText("Passwords do not match.");
                return;
            }

            if (!userService.usernameAvailable(username)) {
                textSmall.setText("Username is already in use.");
                return;
            }

            // Create new user & save
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setHashedPassword(passwordEncoder.encode(password));
            newUser.setRoles(Set.of(Role.USER));

            userService.save(newUser);
            UI.getCurrent().navigate("login");
        });

        buttonSecondary.addClickListener(e -> UI.getCurrent().navigate("login"));
    }
}
