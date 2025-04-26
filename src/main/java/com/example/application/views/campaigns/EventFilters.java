package com.example.application.views.campaigns;

import com.example.application.data.Event;
import com.example.application.data.ReoccurrenceType;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EventFilters extends VerticalLayout implements Specification<Event> {

    private final Long campaignId;
    private TextField nameField = new TextField(getTranslation("name"));
    private TextField placeField = new TextField(getTranslation("place"));
    private TextField areaField = new TextField(getTranslation("area"));
    private TextField typeField = new TextField(getTranslation("event_type"));
    private Select<ReoccurrenceType> reoccurrenceTypeSelect = new Select<>();
    private TextField startDay = new TextField(getTranslation("start_day"));
    private TextField startMonth = new TextField(getTranslation("start_month"));
    private TextField startYear = new TextField(getTranslation("start_year"));
    private TextField between = new TextField(" - ");
    private TextField endDay = new TextField(getTranslation("end_day"));
    private TextField endMonth = new TextField(getTranslation("end_month"));
    private TextField endYear = new TextField(getTranslation("end_year"));

    private Button searchButton = new Button(getTranslation("search"));
    private Button clearButton = new Button(getTranslation("clear"));

    public EventFilters(Long campaignId, Runnable onSearch) {
        this.campaignId = campaignId;

        nameField.setPlaceholder(getTranslation("search_by_name"));
        nameField.setClearButtonVisible(true);

        placeField.setPlaceholder(getTranslation("search_by_place"));
        placeField.setClearButtonVisible(true);

        areaField.setPlaceholder(getTranslation("search_by_area"));
        areaField.setClearButtonVisible(true);

        typeField.setPlaceholder(getTranslation("search_by_type"));
        typeField.setClearButtonVisible(true);

        reoccurrenceTypeSelect.setPlaceholder(getTranslation("search_by_reoccurrence"));
        reoccurrenceTypeSelect.setLabel(getTranslation("reoccurrence"));
        reoccurrenceTypeSelect.setEmptySelectionAllowed(true);
        // reoccurrenceTypeSelect.setItemLabelGenerator(type -> UI.getCurrent().getTranslation("reoccurrence." + type.name().toLowerCase()));
        reoccurrenceTypeSelect.setItems(ReoccurrenceType.values());

        startDay.setClearButtonVisible(true);
        startDay.setWidth("75px");
        startMonth.setClearButtonVisible(true);
        startMonth.setWidth("75px");
        startYear.setClearButtonVisible(true);
        startYear.setWidth("115px");

        between.setValue(" - ");
        between.setWidth("50px");
        between.setEnabled(false);

        endDay.setClearButtonVisible(true);
        endDay.setWidth("75px");
        endMonth.setClearButtonVisible(true);
        endMonth.setWidth("75px");
        endYear.setClearButtonVisible(true);
        endYear.setWidth("115px");

        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        searchButton.addClickListener(e -> onSearch.run());
        clearButton.addClickListener(e -> {
            nameField.clear();
            placeField.clear();
            areaField.clear();
            typeField.clear();
            reoccurrenceTypeSelect.clear();
            startDay.clear();
            startMonth.clear();
            startYear.clear();
            endDay.clear();
            endMonth.clear();
            endYear.clear();
            onSearch.run();
        });

        HorizontalLayout searchByDate = new HorizontalLayout(startDay, startMonth, startYear, between, endDay, endMonth, endYear);
        HorizontalLayout buttonsLayout = new HorizontalLayout(searchButton, clearButton); // TODO: Place lower?
        HorizontalLayout fieldsLayout = new HorizontalLayout(
                nameField, placeField, areaField, typeField, reoccurrenceTypeSelect, searchByDate, buttonsLayout
        );
        fieldsLayout.setWidthFull();


        add(fieldsLayout);
    }

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("campaign").get("id"), campaignId));

        if (!nameField.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + nameField.getValue().toLowerCase() + "%"));
        }
        if (!placeField.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.join("place").get("placeName")), "%" + placeField.getValue().toLowerCase() + "%"));
        }
        if (!areaField.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.join("place").join("area").get("areaName")), "%" + areaField.getValue().toLowerCase() + "%"));
        }
        if (!typeField.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.join("type").get("eventTypeName")), "%" + typeField.getValue().toLowerCase() + "%"));
        }
        if (!reoccurrenceTypeSelect.isEmpty()) {
            predicates.add(cb.equal(root.get("reoccurring"), reoccurrenceTypeSelect.getValue()));
        }

        System.out.println(predicates);
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
