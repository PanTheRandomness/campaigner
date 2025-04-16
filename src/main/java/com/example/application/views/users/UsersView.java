package com.example.application.views.users;

import com.example.application.data.User;
import com.example.application.data.Campaign;
import com.example.application.data.repositories.UserRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.stream.Collectors;

@PageTitle("Users")
@Route("users")
@Menu(order = 6, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Composite<VerticalLayout> {

    private final UserRepository userRepository;

    public UsersView(UserRepository userRepository) {
        this.userRepository = userRepository;

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
}
