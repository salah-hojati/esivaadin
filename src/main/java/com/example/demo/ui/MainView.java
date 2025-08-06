package com.example.demo.ui;

import com.example.demo.model.Word;
import com.example.demo.service.WordService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
// Add this import
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
public class MainView extends VerticalLayout {

    private final WordService wordService;
    private Word currentWord;

    public MainView(@Autowired WordService wordService) {
        this.wordService = wordService;

        getElement().getStyle().set("direction", "rtl");
        setAlignItems(Alignment.CENTER);
        setSpacing(true);

        // Add a link to the new admin view
        add(new RouterLink("Manage Words", WordAdminView.class));

        nextWord();
    }

    private void nextWord() {
        // ... rest of the file is unchanged
        currentWord = wordService.getRandomWord();

        if (currentWord == null) {
            add("No words found in the database.");
            return;
        }

        removeAll();

        // Add the link again after removeAll()
        add(new RouterLink("Manage Words", WordAdminView.class));

        var label = new com.vaadin.flow.component.html.Label("ترجمه انگلیسی: " + currentWord.getFarsi());
        var input = new TextField("English word");
        var button = new Button("Check");

        button.addClickListener(e -> check(input.getValue()));
        input.addKeyPressListener(Key.ENTER, e -> check(input.getValue()));

        add(label, input, button);
        input.focus();
    }

    private void check(String answer) {
        if (answer.trim().equalsIgnoreCase(currentWord.getEnglish())) {
            Notification.show("✅ Correct!");
            nextWord();
        } else {
            Notification.show("❌ Try again.");
        }
    }
}