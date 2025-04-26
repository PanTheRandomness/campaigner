package com.example.application.util;

import com.example.application.data.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class UIRegistryWithUserGrid {
    public static class UIWithUserGrid {
        private final UI ui;
        private final Grid<User> userGrid;

        public UIWithUserGrid(UI ui, Grid<User> userGrid) {
            this.ui = ui;
            this.userGrid = userGrid;
        }

        public UI getUi() {
            return ui;
        }

        public Grid<User> getUserGrid() {
            return userGrid;
        }
    }

    private static final Set<UIWithUserGrid> activeUIs = new CopyOnWriteArraySet<>();

    public static void register(UI ui, Grid<User> userGrid) {
        activeUIs.add(new UIWithUserGrid(ui, userGrid));
        ui.addDetachListener(event -> activeUIs.removeIf(u -> u.getUi().equals(ui)));
    }

    public static Set<UIWithUserGrid> getActiveUIs() {
        return activeUIs;
    }
}
