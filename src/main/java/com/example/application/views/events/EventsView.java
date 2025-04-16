package com.example.application.views.events;

import com.example.application.data.Event;
import com.example.application.data.ReoccurrenceType;
import com.example.application.services.EventService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import java.util.Set;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Events")
@Route("events/:eventID?/:action?(edit)")
@Menu(order = 4, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@PermitAll
@Uses(Icon.class)
@Uses(Icon.class)
public class EventsView extends Div implements BeforeEnterObserver {

    // TODO: Fix ERROR 500
    private final String EVENT_ID = "eventID";
    private final String EVENT_EDIT_ROUTE_TEMPLATE = "events/%s/edit";

    private final Grid<Event> grid = new Grid<>(Event.class, false);

    private TextField name, description, type, location;
    private DatePicker time;
    private Checkbox reoccurring, private_;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Event> binder;

    private Event event;

    private final EventService eventService;

    public EventsView(EventService eventService) {
        this.eventService = eventService;
        addClassNames("events-view");

        // Split layout for grid and editor
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);

        // Configure grid
        configureGrid();

        // Configure form binder
        binder = new BeanValidationBinder<>(Event.class);
        binder.bindInstanceFields(this);

        // Button actions
        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> saveEvent());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> eventId = event.getRouteParameters().get(EVENT_ID).map(Long::parseLong);
        if (eventId.isPresent()) {
            eventService.get(eventId.get()).ifPresentOrElse(
                    this::populateForm,
                    () -> {
                        Notification.show("Event not found", 3000, Position.BOTTOM_START);
                        refreshGrid();
                        event.forwardTo(EventsView.class);
                    }
            );
        }
    }

    private void configureGrid() {
        grid.addColumn("name").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("type").setAutoWidth(true);
        grid.addColumn("time").setAutoWidth(true);
        grid.addColumn("location").setAutoWidth(true);

        // Reoccurring column renderer
        LitRenderer<Event> reoccurringRenderer = LitRenderer.<Event>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>"
        ).withProperty("icon", event -> {
            Set<ReoccurrenceType> types = event.getReoccurring();
            return (types != null && !types.isEmpty() && !types.contains(ReoccurrenceType.NONE)) ? "check" : "minus";
        }).withProperty("color", event -> {
            Set<ReoccurrenceType> types = event.getReoccurring();
            return (types != null && !types.isEmpty() && !types.contains(ReoccurrenceType.NONE))
                    ? "var(--lumo-primary-text-color)"
                    : "var(--lumo-disabled-text-color)";
        });

        grid.addColumn(reoccurringRenderer).setHeader("Reoccurring").setAutoWidth(true);

        // Private column renderer
        LitRenderer<Event> privateRenderer = LitRenderer.<Event>of(
                        "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>"
                ).withProperty("icon", private_ -> private_.isPrivate_() ? "check" : "minus")
                .withProperty("color", private_ -> private_.isPrivate_() ? "var(--lumo-primary-text-color)" : "var(--lumo-disabled-text-color)");

        grid.addColumn(privateRenderer).setHeader("Private").setAutoWidth(true);

        grid.setItems(query -> eventService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EVENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EventsView.class);
            }
        });
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        name = new TextField("Name");
        description = new TextField("Description");
        type = new TextField("Type");
        time = new DatePicker("Time");
        location = new TextField("Location");
        reoccurring = new Checkbox("Reoccurring");
        private_ = new Checkbox("Private");

        formLayout.add(name, description, type, time, location, reoccurring, private_);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Event value) {
        this.event = value;
        binder.readBean(this.event);
    }

    private void saveEvent() {
        try {
            if (this.event == null) {
                this.event = new Event();
            }
            binder.writeBean(this.event);
            eventService.save(this.event);
            clearForm();
            refreshGrid();
            Notification.show("Event saved successfully");
            UI.getCurrent().navigate(EventsView.class);
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification.show("Error: Record was updated by someone else.", 3000, Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to save. Please check the form values.");
        }
    }
}
