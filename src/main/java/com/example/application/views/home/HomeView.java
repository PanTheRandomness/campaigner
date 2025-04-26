package com.example.application.views.home;

import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Optional;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@AnonymousAllowed
public class HomeView extends Composite<VerticalLayout> {

    private final AuthenticatedUser authenticatedUser;

    public HomeView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        VerticalLayout mainLayout = getContent();
        mainLayout.setWidthFull();
        mainLayout.getStyle().set("flex-grow", "1");

        // Title
        H1 title = new H1("Welcome to Campaigner");
        title.setWidth("max-content");

        // Headers and Descriptions
        FormLayout featureLayout = new FormLayout();
        featureLayout.setWidthFull();
        featureLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("250px", 2),
                new ResponsiveStep("500px", 3)
        );

        featureLayout.add(
                createFeature("Manage Campaigns", "Manage all of your campaigns conveniently in one place!", "/campaigns"),
                // TODO: Link to timeline or remove/modify
                createFeature("Keep track of your plot with timelines", "Create new events or modify old ones. Keep track of important plot points visually.", "/timeline"),
                createFeature("Keep notes with Encyclopedia", "Never forget key pieces information about your world's history or geography.", "/encyclopedia")
        );

        // User Status (logged in: Welcome message / logged out: buttons)
        HorizontalLayout userActionLayout = new HorizontalLayout();
        userActionLayout.setWidthFull();
        userActionLayout.addClassName(Gap.MEDIUM);
        userActionLayout.getStyle().set("flex-grow", "1");

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            Paragraph welcomeText = new Paragraph("Welcome back " + maybeUser.get().getName() + "!");
            welcomeText.getStyle().set("font-size", "var(--lumo-font-size-xl)");
            userActionLayout.add(welcomeText);
        } else {
            Button registerButton = new Button("Register");
            registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            registerButton.addClickListener(e -> UI.getCurrent().navigate("register"));

            Paragraph orText = new Paragraph(" or ");
            orText.getStyle().set("font-size", "var(--lumo-font-size-xl)");

            Button loginButton = new Button("Sign in");
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.addClickListener(e -> UI.getCurrent().navigate("login"));

            userActionLayout.add(registerButton, orText, loginButton);
        }

        // Adding components to layout
        VerticalLayout contentLayout = new VerticalLayout(title, featureLayout, userActionLayout);
        contentLayout.setWidthFull();
        contentLayout.setFlexGrow(1, featureLayout, userActionLayout);

        mainLayout.add(contentLayout);
    }

    private VerticalLayout createFeature(String heading, String description, String route) {
        VerticalLayout feature = new VerticalLayout();
        feature.setWidthFull();

        Anchor link = new Anchor(route, heading);
        link.getElement().getStyle().set("font-size", "var(--lumo-font-size-xl)");
        link.getElement().getStyle().set("font-weight", "bold");
        link.getElement().getStyle().set("text-decoration", "none");
        link.getElement().getStyle().set("color", "var(--lumo-primary-text-color)");

        Paragraph text = new Paragraph(description);
        text.setWidthFull();
        text.getStyle().set("font-size", "var(--lumo-font-size-m)");

        feature.add(link, text);
        return feature;
    }
}
