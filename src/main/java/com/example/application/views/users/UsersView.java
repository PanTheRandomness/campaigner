package com.example.application.views.users;

import com.example.application.data.User;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.RolesAllowed;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Users")
@Route("users")
@Menu(order = 6, icon = LineAwesomeIconUrl.USER_FRIENDS_SOLID)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Composite<VerticalLayout> {

    public UsersView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Grid<User> userGrid = new Grid<>(User.class);
        HorizontalLayout layoutRow = new HorizontalLayout();
        Paragraph textSmall = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        userGrid.setWidth("100%");
        userGrid.setHeight("100%");
        setGridSampleData(userGrid);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        textSmall.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        getContent().add(layoutColumn2);
        layoutColumn2.add(userGrid);
        getContent().add(layoutRow);
        layoutRow.add(textSmall);
    }

    private void setGridSampleData(Grid<User> grid) {
        // TODO: Populate User Grid
        grid.setItems();
    }
}
