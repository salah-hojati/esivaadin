package com.example.demo.ui;

import com.example.demo.model.Word;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

/**
 * A form for editing the details of a Word.
 */
public class WordForm extends FormLayout {

    TextField farsi = new TextField("Farsi");
    TextField english = new TextField("English");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    // Binder connects the form fields to the Word data object
    Binder<Word> binder = new BeanValidationBinder<>(Word.class);

    public WordForm() {
        // Set direction to RTL for the form fields and layout
        getElement().getStyle().set("direction", "rtl");

        // Bind the 'farsi' and 'english' fields to the Word class properties
        binder.bindInstanceFields(this);

        // Add the fields and the button layout to the form
        add(farsi, english, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        // Add keyboard shortcuts for convenience
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        // Register listeners that fire custom events
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        // The save button is only enabled when the form is valid
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setWord(Word word) {
        // Binds the given word object to the form
        binder.setBean(word);
    }

    // --- Events ---
    // Define custom events for save, delete, and close actions. This allows
    // the parent view (WordAdminView) to react to user actions.
    public static abstract class WordFormEvent extends ComponentEvent<WordForm> {
        private final Word word;

        protected WordFormEvent(WordForm source, Word word) {
            super(source, false);
            this.word = word;
        }

        public Word getWord() {
            return word;
        }
    }

    public static class SaveEvent extends WordFormEvent {
        SaveEvent(WordForm source, Word word) {
            super(source, word);
        }
    }

    public static class DeleteEvent extends WordFormEvent {
        DeleteEvent(WordForm source, Word word) {
            super(source, word);
        }
    }

    public static class CloseEvent extends WordFormEvent {
        CloseEvent(WordForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}