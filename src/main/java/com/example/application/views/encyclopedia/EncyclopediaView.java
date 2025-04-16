package com.example.application.views.encyclopedia;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Encyclopedia")
@Route("encyclopedia")
@Menu(order = 5, icon = LineAwesomeIconUrl.BOOK_SOLID)
@PermitAll
public class EncyclopediaView extends Composite<VerticalLayout> {

    //TODO: Add content & Routes to tabs
    public EncyclopediaView() {
        // Create layout & components
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        FormLayout formLayout2Col = new FormLayout();
        H2 EncyclopediaHeader = new H2("Encyclopedia");
        H2 EditorHeader = new H2("Editor");
        Paragraph EncyclopediaText = new Paragraph();
        TextArea textArea = new TextArea("Content Editor");
        HorizontalLayout layoutRow2 = new HorizontalLayout();

        // Style adjustments
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        formLayout2Col.getStyle().set("flex-grow", "1");

        // Configure component widths
        // TODO: Add form to create/edit Encyclopedia items
        // TODO: Create select for item type
        // TODO: Show all encyclopedia items
        // TODO: Add Tabs for each item type?
        EncyclopediaHeader.setText("Heading");
        EncyclopediaHeader.setWidth("max-content");
        EditorHeader.setText("Encyclopedia Item Editor");
        EditorHeader.setWidth("max-content");
        EncyclopediaText.setText(
                "Here you'll find everything...");
        EncyclopediaText.setWidth("100%");
        EncyclopediaText.getStyle().set("font-size", "var(--lumo-font-size-m)");
        textArea.setWidth("100%");
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");

        // Add components to the layout
        getContent().add(layoutRow);
        getContent().add(layoutColumn2);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(EncyclopediaHeader, EditorHeader, EncyclopediaText, textArea);
        getContent().add(layoutRow2);
    }
}
