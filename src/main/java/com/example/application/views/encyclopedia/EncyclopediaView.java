package com.example.application.views.encyclopedia;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
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

    public EncyclopediaView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        Tabs tabs = new Tabs();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        FormLayout formLayout2Col = new FormLayout();
        H2 h2 = new H2();
        H2 h22 = new H2();
        Paragraph textMedium = new Paragraph();
        RichTextEditor richTextEditor = new RichTextEditor();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        Paragraph textSmall = new Paragraph();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        tabs.setWidth("100%");
        setTabsSampleData(tabs);
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        formLayout2Col.getStyle().set("flex-grow", "1");
        h2.setText("Heading");
        h2.setWidth("max-content");
        h22.setText("Heading");
        h22.setWidth("max-content");
        textMedium.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textMedium.setWidth("100%");
        textMedium.getStyle().set("font-size", "var(--lumo-font-size-m)");
        richTextEditor.setWidth("100%");
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        textSmall.setText(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        getContent().add(layoutRow);
        layoutRow.add(tabs);
        getContent().add(layoutColumn2);
        layoutColumn2.add(formLayout2Col);
        formLayout2Col.add(h2);
        formLayout2Col.add(h22);
        formLayout2Col.add(textMedium);
        formLayout2Col.add(richTextEditor);
        getContent().add(layoutRow2);
        layoutRow2.add(textSmall);
    }

    private void setTabsSampleData(Tabs tabs) {
        tabs.add(new Tab("Overview"));
        tabs.add(new Tab("Timeline"));
        tabs.add(new Tab("Players"));
        tabs.add(new Tab("World"));
    }
}
