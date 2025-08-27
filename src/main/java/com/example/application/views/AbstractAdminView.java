package com.example.application.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * An abstract base view for all admin pages.
 * It provides a consistent "Back to Main View" button.
 */
public abstract class AbstractAdminView extends VerticalLayout {
    public AbstractAdminView() {
        Button backButton = new Button("← Back to Main View", e -> UI.getCurrent().navigate(MainView.class));
        HorizontalLayout menu = new HorizontalLayout(backButton);
        menu.getStyle().set("padding-bottom", "20px");
        add(menu);
    }
}