package com.example.application.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Home")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@AnonymousAllowed
public class HomeView extends Composite<VerticalLayout> {

    // TODO: Add content to home page
    public HomeView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H1 h1 = new H1("Welcome to Campaigner");
        VerticalLayout layoutColumn3 = new VerticalLayout();
        FormLayout formLayout3Col = new FormLayout();
        H3 campiaignH3 = new H3("Manage Campaigns");
        H3 timelineH3 = new H3("Keep track of your plot with timelines");
        H3 encyclopediaH3 = new H3("Keep notes with Encyclopedia");

        Paragraph campaignsText = new Paragraph();
        Paragraph timelineText = new Paragraph();
        Paragraph encyclopediaText = new Paragraph();

        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        // TODO: Conditional Buttons/Welcome text
        Button registerButton = new Button("Register");
        Paragraph orText = new Paragraph();
        Button loginButton = new Button("login");
        Paragraph welcomeText = new Paragraph();

        HorizontalLayout layoutRow3 = new HorizontalLayout();
        Paragraph footerText = new Paragraph();
        getContent().setWidth("100%");

        if (UI.getCurrent().getSession().getAttribute("user") != null) {
            String username = (String) UI.getCurrent().getSession().getAttribute("user");
            welcomeText.setText("Welcome back " + username + "!");
            layoutRow2.add(welcomeText);
        } else {
            layoutRow2.add(registerButton, orText, loginButton, welcomeText);
        }

        getContent().getStyle().set("flex-grow", "1");

        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");

        h1.setText("Welcome to Campaigner");
        h1.setWidth("max-content");

        layoutColumn3.setWidthFull();
        layoutColumn2.setFlexGrow(1.0, layoutColumn3);
        layoutColumn3.setWidth("100%");
        layoutColumn3.getStyle().set("flex-grow", "1");

        formLayout3Col.setWidth("100%");
        formLayout3Col.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("250px", 2),
                new ResponsiveStep("500px", 3));

        campiaignH3.setText("Manage Campaigns");
        campiaignH3.setWidth("max-content");
        timelineH3.setText("Keep track of your plot with timelines");
        timelineH3.setWidth("max-content");
        encyclopediaH3.setText("Keep notes with Encyclopedia");
        encyclopediaH3.setWidth("max-content");

        campaignsText.setText(
                "Manage all of your campaigns conveniently in one place!");
        campaignsText.setWidth("100%");
        campaignsText.getStyle().set("font-size", "var(--lumo-font-size-m)");
        timelineText.setText(
                "Create new events or modify old ones. Keep track of important plot points visually.");
        timelineText.setWidth("100%");
        timelineText.getStyle().set("font-size", "var(--lumo-font-size-m)");
        encyclopediaText.setText(
                "Never forget key pieces information about your world's history or geography.");
        encyclopediaText.setWidth("100%");
        encyclopediaText.getStyle().set("font-size", "var(--lumo-font-size-m)");

        layoutRow.setWidthFull();
        layoutColumn3.setFlexGrow(1.0, layoutRow);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn4.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth("100%");
        layoutColumn4.getStyle().set("flex-grow", "1");

        layoutRow2.setWidthFull();
        layoutColumn4.setFlexGrow(1.0, layoutRow2);
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.getStyle().set("flex-grow", "1");

        registerButton.setText("Register");
        registerButton.setWidth("min-content");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        orText.setText(" or ");
        orText.setWidth("min-content");
        orText.getStyle().set("font-size", "var(--lumo-font-size-xl)");

        loginButton.setText("Sign in");
        loginButton.setWidth("min-content");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        welcomeText.setText("to start creating!");
        welcomeText.setWidth("fit-content");
        welcomeText.getStyle().set("font-size", "var(--lumo-font-size-xl)");

        layoutRow3.addClassName(Gap.MEDIUM);
        layoutRow3.setWidth("100%");
        layoutRow3.setHeight("min-content");
        footerText.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        footerText.setWidth("100%");
        footerText.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        getContent().add(layoutColumn2);

        layoutColumn2.add(h1);
        layoutColumn2.add(layoutColumn3);
        layoutColumn3.add(formLayout3Col);
        formLayout3Col.add(campiaignH3);
        formLayout3Col.add(timelineH3);
        formLayout3Col.add(encyclopediaH3);
        formLayout3Col.add(campaignsText);
        formLayout3Col.add(timelineText);
        formLayout3Col.add(encyclopediaText);
        layoutColumn3.add(layoutRow);
        layoutRow.add(layoutColumn4);
        layoutColumn4.add(layoutRow2);
        getContent().add(layoutRow3);
        layoutRow3.add(footerText);

        registerButton.addClickListener(e -> {
            UI.getCurrent().navigate("register");
        });

        loginButton.addClickListener(e -> {
            UI.getCurrent().navigate("login");
        });
    }
}
